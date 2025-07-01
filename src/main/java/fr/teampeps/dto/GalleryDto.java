package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class GalleryDto {
    private String id;
    private String date;
    private List<GalleryPhotoDto> photos;
    private List<String> authors;
    private String thumbnailImageKey;
    private Map<String, GalleryTranslationDto> translations;
}
