package fr.teampeps.mapper;

import fr.teampeps.dto.GalleryPhotoDto;
import fr.teampeps.models.GalleryPhoto;
import org.springframework.stereotype.Component;

@Component
public class GalleryPhotoMapper {

    public GalleryPhotoDto toGalleryPhotoDto(GalleryPhoto galleryPhoto) {
        return GalleryPhotoDto.builder()
                .id(galleryPhoto.getId())
                .imageKey(galleryPhoto.getImageKey())
                .author(galleryPhoto.getAuthor())
                .build();
    }
}
