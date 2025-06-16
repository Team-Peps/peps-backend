package fr.teampeps.service;

import fr.teampeps.dto.SliderDto;
import fr.teampeps.mapper.SliderMapper;
import fr.teampeps.enums.Bucket;
import fr.teampeps.models.Slider;
import fr.teampeps.models.SliderTranslation;
import fr.teampeps.record.SliderRequest;
import fr.teampeps.repository.SliderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SliderService {

    private final SliderRepository sliderRepository;
    private final SliderMapper sliderMapper;
    private final MinioService minioService;

    public Map<String, List<SliderDto>> getAllSliders() {

        List<SliderDto> slidersActive = sliderRepository.findAllByIsActiveOrderByOrder((true)).stream()
                .map(sliderMapper::toSliderDto)
                .toList();

        List<SliderDto> slidersInactive = sliderRepository.findAllByIsActive(false).stream()
                .map(sliderMapper::toSliderDto)
                .toList();

        return Map.of(
                "activeSliders", slidersActive,
                "inactiveSliders", slidersInactive
        );
    }

    public List<SliderDto> getAllActiveSlider() {
        return sliderRepository.findAllByIsActiveOrderByOrder(true).stream()
                .map(sliderMapper::toSliderDto)
                .toList();
    }

    public SliderDto saveSlider(
            SliderRequest sliderRequest,
            MultipartFile imageFileFr,
            MultipartFile mobileImageFileFr,
            MultipartFile imageFileEn,
            MultipartFile mobileImageFileEn
    ) {
        if(imageFileFr == null || mobileImageFileFr == null || imageFileEn == null || mobileImageFileEn == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il faut fournir les quatre images");
        }

        Slider slider = sliderMapper.toSlider(sliderRequest);

        try {
            String imageUrlFr = minioService.uploadImageFromMultipartFile(imageFileFr, slider.getId() + "_fr", Bucket.SLIDERS);
            String imageUrlEn = minioService.uploadImageFromMultipartFile(imageFileEn, slider.getId() + "_en", Bucket.SLIDERS);
            String mobileImageUrlFr = minioService.uploadImageFromMultipartFile(mobileImageFileFr, slider.getId() + "_mobile_fr", Bucket.SLIDERS);
            String mobileImageUrlEn = minioService.uploadImageFromMultipartFile(mobileImageFileEn, slider.getId() + "_mobile_en", Bucket.SLIDERS);

            List<SliderTranslation> validTranslations = slider.getTranslations().stream()
                .filter(t -> t.getLang() != null && !t.getLang().isBlank() && t.getCtaLabel() != null && !t.getCtaLabel().isBlank())
                .peek(sliderTranslation -> {
                    sliderTranslation.setParent(slider);
                    switch (sliderTranslation.getLang().toLowerCase()) {
                        case "fr" -> {
                            sliderTranslation.setImageKey(imageUrlFr);
                            sliderTranslation.setMobileImageKey(mobileImageUrlFr);
                        }
                        case "en" -> {
                            sliderTranslation.setImageKey(imageUrlEn);
                            sliderTranslation.setMobileImageKey(mobileImageUrlEn);
                        }
                        default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Langue non supportée : " + sliderTranslation.getLang());
                    }
                })
                .toList();

            slider.setTranslations(validTranslations);

            long newOrder = sliderRepository.count();
            slider.setOrder(newOrder);

            return sliderMapper.toSliderDto(sliderRepository.save(slider));

        } catch (Exception e) {
            log.error("Error saving slider with ID: {}", sliderRequest.ctaLink(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la sauvegarde du slider", e);
        }
    }

    public SliderDto updateSlider(
            SliderRequest sliderRequest,
            MultipartFile imageFileFr,
            MultipartFile mobileImageFileFr,
            MultipartFile imageFileEn,
            MultipartFile mobileImageFileEn
    ) {

        Slider existingSlider = sliderRepository.findById(sliderRequest.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slider non trouvé"));

        Map<String, SliderTranslation> translationsByLang = existingSlider.getTranslations().stream()
                        .collect(Collectors.toMap(t -> t.getLang().toLowerCase(), Function.identity()));

        sliderRequest.translations().forEach((lang, tRequest) -> {
            SliderTranslation translation = translationsByLang.get(lang.toLowerCase());
            if(translation != null) {
                translation.setCtaLabel(tRequest.ctaLabel());
            }
        });

        existingSlider.setCtaLink(sliderRequest.ctaLink());
        existingSlider.setIsActive(sliderRequest.isActive());

        try {

            uploadImageIfPresent(imageFileFr, existingSlider.getId() + "_fr", "fr", translationsByLang, true);
            uploadImageIfPresent(mobileImageFileFr, existingSlider.getId() + "_mobile_fr", "fr", translationsByLang, false);
            uploadImageIfPresent(imageFileEn, existingSlider.getId() + "_en", "en", translationsByLang, true);
            uploadImageIfPresent(mobileImageFileEn, existingSlider.getId() + "_mobile_en", "en", translationsByLang, false);

            return sliderMapper.toSliderDto(sliderRepository.save(existingSlider));

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du slider", e);
        }
    }

    public void deleteSlider(String id) {
        try {
            sliderRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Slider non trouvé", e);
        }
    }

    public SliderDto toggleActive(String id) {
        Slider slider = sliderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slider non trouvé"));

        slider.setIsActive(!slider.getIsActive());

        if(!slider.getIsActive()) {
            slider.setOrder(-1L);
        }

        return sliderMapper.toSliderDto(sliderRepository.save(slider));
    }

    public void updateSliderOrder(List<String> orderedIds) {
        for (int i = 0; i < orderedIds.size(); i++) {
            String id = orderedIds.get(i);
            int finalI = i;
            sliderRepository.findById(id).ifPresent(slider -> {
                slider.setOrder((long) finalI);
                sliderRepository.save(slider);
            });
        }
    }

    private void uploadImageIfPresent(
            MultipartFile file,
            String objectName,
            String lang,
            Map<String, SliderTranslation> translationsByLang,
            boolean isDesktop
    ) {
        if(file != null && !file.isEmpty()) {
            String imageKey = minioService.uploadImageFromMultipartFile(file, objectName, Bucket.SLIDERS);
            SliderTranslation translation = translationsByLang.get(lang.toLowerCase());
            if(translation != null) {
                if(isDesktop) {
                    translation.setImageKey(imageKey);
                } else {
                    translation.setMobileImageKey(imageKey);
                }
            }
        }
    }

}
