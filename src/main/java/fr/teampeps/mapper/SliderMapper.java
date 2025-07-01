package fr.teampeps.mapper;

import fr.teampeps.dto.SliderDto;
import fr.teampeps.dto.SliderTranslationDto;
import fr.teampeps.models.Slider;
import fr.teampeps.models.SliderTranslation;
import fr.teampeps.record.SliderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SliderMapper {

    public SliderDto toSliderDto(Slider slider) {
        Map<String, SliderTranslationDto> translationsMap = slider.getTranslations().stream()
                .collect(Collectors.toMap(
                        SliderTranslation::getLang,
                        t -> SliderTranslationDto.builder()
                                .ctaLabel(t.getCtaLabel())
                                .imageKey(t.getImageKey())
                                .mobileImageKey(t.getMobileImageKey())
                                .build()
                ));

        return SliderDto.builder()
                .id(slider.getId())
                .isActive(slider.getIsActive())
                .ctaLink(slider.getCtaLink())
                .order(slider.getOrder())
                .translations(translationsMap)
                .build();
    }

    public Slider toSlider(SliderRequest sliderRequest) {
        return Slider.builder()
                .ctaLink(sliderRequest.ctaLink())
                .isActive(sliderRequest.isActive())
                .translations(sliderRequest.translations().entrySet().stream()
                        .map(entry -> {
                            SliderTranslation translation = new SliderTranslation();
                            translation.setLang(entry.getKey());
                            translation.setCtaLabel(entry.getValue().ctaLabel());
                            return translation;
                        })
                        .toList())
                .id(sliderRequest.id() != null ? sliderRequest.id() : UUID.randomUUID().toString())
                .build();

    }
}
