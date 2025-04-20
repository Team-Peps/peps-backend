package fr.teampeps.dto;

import fr.teampeps.enums.ArticleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleDto {
    private String id;
    private String title;
    private String content;
    private String imageKey;
    private String thumbnailImageKey;
    private String createdAt;
    private ArticleType articleType;
}
