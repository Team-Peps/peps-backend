package fr.teampeps.mapper;

import fr.teampeps.dto.GalleryDto;
import fr.teampeps.dto.GalleryWithAuthorsDto;
import fr.teampeps.models.Gallery;
import fr.teampeps.models.GalleryPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GalleryMapper {

    private final GalleryPhotoMapper galleryPhotoMapper;

    public GalleryDto toGalleryDto(Gallery gallery) {
        return GalleryDto.builder()
                .id(gallery.getId())
                .eventName(gallery.getEventName())
                .date(gallery.getDate().toString())
                .description(gallery.getDescription())
                .photos(gallery.getPhotos().stream()
                        .map(galleryPhotoMapper::toGalleryPhotoDto)
                        .toList())
                .build();
    }

    public GalleryWithAuthorsDto toGalleryWithAuthorsDto(Gallery gallery) {
        return GalleryWithAuthorsDto.builder()
                .id(gallery.getId())
                .eventName(gallery.getEventName())
                .date(gallery.getDate().toString())
                .description(gallery.getDescription())
                .authors(gallery.getPhotos().stream()
                        .map(GalleryPhoto::getAuthor)
                        .distinct()
                        .toList())
                .photos(gallery.getPhotos().stream()
                        .map(galleryPhotoMapper::toGalleryPhotoDto)
                        .toList())
                .build();
    }
}
