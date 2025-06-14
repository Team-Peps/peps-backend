package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SliderTranslationDto {
    private String ctaLabel;
    private String imageKey;
    private String mobileImageKey;
}

