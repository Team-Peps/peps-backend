package fr.teampeps.record;

import java.time.LocalDate;
import java.util.Map;

public record GalleryRequest(
    LocalDate date,
    String thumbnailImageKey,
    Map<String, GalleryTranslationRequest> translations
) {
}
