package fr.teampeps.mapper;

import fr.teampeps.dto.ArticleDto;
import fr.teampeps.dto.ArticleTinyDto;
import fr.teampeps.model.article.Article;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleMapper {

    public ArticleDto toArticleDto(Article article) {
        return ArticleDto.builder()
                .articleType(article.getArticleType())
                .content(article.getContent())
                .createdAt(article.getCreatedAt().toString())
                .id(article.getId())
                .thumbnailImageKey(article.getThumbnailImageKey())
                .imageKey(article.getImageKey())
                .title(article.getTitle())
                .build();
    }

    public ArticleTinyDto toArticleTinyDto(Article article) {
        String plainTextContent = Jsoup.parse(article.getContent()).text();
        return ArticleTinyDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .thumbnailImageKey(article.getThumbnailImageKey())
                .shortContent(plainTextContent.length() > 133 ? plainTextContent.substring(0, 134) + "..." : plainTextContent)
                .build();
    }
}