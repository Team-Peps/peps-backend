package fr.teampeps.service;

import fr.teampeps.dto.SliderDto;
import fr.teampeps.dto.SliderTinyDto;
import fr.teampeps.mapper.SliderMapper;
import fr.teampeps.enums.Bucket;
import fr.teampeps.models.Slider;
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
import java.util.UUID;

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

    public List<SliderTinyDto> getAllActiveSlider() {
        return sliderRepository.findAllByIsActiveOrderByOrder(true).stream()
                .map(sliderMapper::toSliderTinyDto)
                .toList();
    }

    public SliderDto saveSlider(Slider slider, MultipartFile imageFile, MultipartFile mobileImageFile) {
        if(imageFile == null || mobileImageFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il faut fournir les deux images");
        }

        try {
            String sliderId = UUID.randomUUID().toString();

            String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, sliderId, Bucket.SLIDERS);
            String mobileImageUrl = minioService.uploadImageFromMultipartFile(mobileImageFile, sliderId + "_mobile", Bucket.SLIDERS);
            slider.setImageKey(imageUrl);
            slider.setMobileImageKey(mobileImageUrl);

            long newOrder = sliderRepository.count();
            slider.setOrder(newOrder);

            return sliderMapper.toSliderDto(sliderRepository.save(slider));

        } catch (Exception e) {
            log.error("Error saving slider with ID: {}", slider.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la sauvegarde du slider", e);
        }
    }

    public SliderDto updateSlider(Slider slider, MultipartFile imageFile, MultipartFile mobileImageFile) {

        Slider existingSlider = sliderRepository.findById(slider.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slider non trouvé"));
        slider.setOrder(existingSlider.getOrder());

        try {
            if(imageFile != null) {
                String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, slider.getId(), Bucket.SLIDERS);
                slider.setImageKey(imageUrl);
            }

            if(mobileImageFile != null) {
                String mobileImageUrl = minioService.uploadImageFromMultipartFile(mobileImageFile, slider.getId() + "_mobile", Bucket.SLIDERS);
                slider.setMobileImageKey(mobileImageUrl);
            }

            return sliderMapper.toSliderDto(sliderRepository.save(slider));

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

}
