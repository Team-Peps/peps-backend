package fr.teampeps.record;

import fr.teampeps.enums.ArticleType;

import java.time.LocalDate;
import java.util.Map;

public record ArticleRequest(
        String id,
        String thumbnailImageKey,
        String imageKey,
        LocalDate createdAt,
        ArticleType articleType,
        Map<String, ArticleTranslationRequest> translations
) {
}
