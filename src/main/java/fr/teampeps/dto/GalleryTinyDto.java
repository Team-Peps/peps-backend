package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class GalleryTinyDto {
    private String id;
    private String date;
    private String thumbnailImageKey;
    private Map<String, GalleryTranslationDto> translations;
}
