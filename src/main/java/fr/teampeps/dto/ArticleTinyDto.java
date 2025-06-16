package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ArticleTinyDto {
    private String id;
    private String thumbnailImageKey;
    private Map<String, ArticleTranslationDto> translations;
}
