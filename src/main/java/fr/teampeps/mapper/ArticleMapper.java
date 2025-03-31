package fr.teampeps.mapper;

import fr.teampeps.dto.ArticleDto;
import fr.teampeps.model.article.Article;
import lombok.RequiredArgsConstructor;
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
                .imageKey(article.getImageKey())
                .title(article.getTitle())
                .build();
    }
}
