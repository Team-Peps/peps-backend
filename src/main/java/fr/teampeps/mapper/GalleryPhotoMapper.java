package fr.teampeps.mapper;

import fr.teampeps.dto.GalleryPhotoDto;
import fr.teampeps.models.GalleryPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GalleryPhotoMapper {

    private final AuthorMapper authorMapper;

    public GalleryPhotoDto toGalleryPhotoDto(GalleryPhoto galleryPhoto) {
        return GalleryPhotoDto.builder()
                .id(galleryPhoto.getId())
                .imageKey(galleryPhoto.getImageKey())
                .author(authorMapper.toDto(galleryPhoto.getAuthor()))
                .build();
    }
}
