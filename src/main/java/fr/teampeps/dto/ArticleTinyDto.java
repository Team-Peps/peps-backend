package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleTinyDto {
    private String id;
    private String title;
    private String shortContent;
    private String thumbnailImageKey;
}
