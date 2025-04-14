package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SliderDto {
    private String id;
    private String imageKey;
    private String mobileImageKey;
    private Boolean isActive;
    private String ctaLink;
    private String ctaLabel;
    private Long order;
}
