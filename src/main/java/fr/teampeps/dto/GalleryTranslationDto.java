package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GalleryTranslationDto {
    private String eventName;
    private String description;
}
