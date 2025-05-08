package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GalleryPhotoDto {
    private String id;
    private String imageKey;
    private String author;
}
