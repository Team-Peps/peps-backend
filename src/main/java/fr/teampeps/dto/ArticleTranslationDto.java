package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleTranslationDto {
    private String title;
    private String content;
}
