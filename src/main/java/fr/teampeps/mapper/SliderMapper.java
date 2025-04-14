package fr.teampeps.mapper;

import fr.teampeps.dto.SliderDto;
import fr.teampeps.dto.SliderTinyDto;
import fr.teampeps.model.Slider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SliderMapper {

    public SliderDto toSliderDto(Slider slider) {
        return SliderDto.builder()
                .id(slider.getId())
                .imageKey(slider.getImageKey())
                .mobileImageKey(slider.getMobileImageKey())
                .isActive(slider.getIsActive())
                .ctaLink(slider.getCtaLink())
                .ctaLabel(slider.getCtaLabel())
                .order(slider.getOrder())
                .build();
    }

    public SliderTinyDto toSliderTinyDto(Slider slider) {
        return SliderTinyDto.builder()
                .id(slider.getId())
                .imageKey(slider.getImageKey())
                .mobileImageKey(slider.getMobileImageKey())
                .ctaLink(slider.getCtaLink())
                .ctaLabel(slider.getCtaLabel())
                .order(slider.getOrder())
                .build();
    }
}
