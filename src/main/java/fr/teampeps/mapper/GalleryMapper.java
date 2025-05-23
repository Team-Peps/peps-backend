package fr.teampeps.mapper;

import fr.teampeps.dto.GalleryDto;
import fr.teampeps.dto.GalleryTinyDto;
import fr.teampeps.models.Author;
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
                .thumbnailImageKey(gallery.getThumbnailImageKey())
                .photos(gallery.getPhotos().stream()
                        .map(galleryPhotoMapper::toGalleryPhotoDto)
                        .toList())
                .authors(gallery.getPhotos().stream()
                        .map(GalleryPhoto::getAuthor)
                        .distinct()
                        .map(Author::getName)
                        .toList())
                .build();
    }

    public GalleryTinyDto toGalleryTinyDto(Gallery gallery) {
        return GalleryTinyDto.builder()
                .id(gallery.getId())
                .eventName(gallery.getEventName())
                .date(gallery.getDate().toString())
                .description(gallery.getDescription().length() > 133 ? gallery.getDescription().substring(0, 134) + "..." : gallery.getDescription())
                .thumbnailImageKey(gallery.getThumbnailImageKey())
                .build();
    }
}
