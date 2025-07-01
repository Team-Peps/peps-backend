package fr.teampeps.mapper;

import fr.teampeps.dto.GalleryDto;
import fr.teampeps.dto.GalleryTinyDto;
import fr.teampeps.dto.GalleryTranslationDto;
import fr.teampeps.models.Author;
import fr.teampeps.models.Gallery;
import fr.teampeps.models.GalleryPhoto;
import fr.teampeps.models.GalleryTranslation;
import fr.teampeps.record.GalleryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GalleryMapper {

    private final GalleryPhotoMapper galleryPhotoMapper;

    public GalleryDto toGalleryDto(Gallery gallery) {
        Map<String, GalleryTranslationDto> translationsDto = gallery.getTranslations().stream()
                .collect(Collectors.toMap(
                        GalleryTranslation::getLang,
                        t -> GalleryTranslationDto.builder()
                                .eventName(t.getEventName())
                                .description(t.getDescription())
                                .build()
                ));

        return GalleryDto.builder()
                .id(gallery.getId())
                .date(gallery.getDate().toString())
                .thumbnailImageKey(gallery.getThumbnailImageKey())
                .photos(gallery.getPhotos().stream()
                        .map(galleryPhotoMapper::toGalleryPhotoDto)
                        .toList())
                .authors(gallery.getPhotos().stream()
                        .map(GalleryPhoto::getAuthor)
                        .distinct()
                        .map(Author::getName)
                        .toList())
                .translations(translationsDto)
                .build();
    }

    public GalleryTinyDto toGalleryTinyDto(Gallery gallery) {
        Map<String, GalleryTranslationDto> translationsDto = gallery.getTranslations().stream()
                .collect(Collectors.toMap(
                        GalleryTranslation::getLang,
                        t -> GalleryTranslationDto.builder()
                                .eventName(t.getEventName())
                                .description(t.getDescription().length() > 133 ? t.getDescription().substring(0, 134) + "..." : t.getDescription())
                                .build()
                ));

        return GalleryTinyDto.builder()
                .id(gallery.getId())
                .date(gallery.getDate().toString())
                .thumbnailImageKey(gallery.getThumbnailImageKey())
                .translations(translationsDto)
                .build();
    }

    public Gallery toGallery(GalleryRequest galleryRequest) {
        return Gallery.builder()
                .date(galleryRequest.date())
                .thumbnailImageKey(galleryRequest.thumbnailImageKey())
                .translations(galleryRequest.translations().entrySet().stream()
                        .map(entry -> {
                            GalleryTranslation translation = new GalleryTranslation();
                            translation.setLang(entry.getKey());
                            translation.setEventName(entry.getValue().eventName());
                            translation.setDescription(entry.getValue().description());
                            return translation;
                        }).collect(Collectors.toList()))
                .build();
    }
}
