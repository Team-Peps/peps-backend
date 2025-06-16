package fr.teampeps.dto;

import fr.teampeps.enums.ArticleType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ArticleDto {
    private String id;
    private String imageKey;
    private String thumbnailImageKey;
    private String createdAt;
    private ArticleType articleType;
    private Map<String, ArticleTranslationDto> translations;
}
