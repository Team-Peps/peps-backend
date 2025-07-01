package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.Map;

@Data
@Builder
public class SliderDto {
    private String id;
    @Nullable
    private Boolean isActive;
    private String ctaLink;
    private Long order;
    private Map<String, SliderTranslationDto> translations;
}
