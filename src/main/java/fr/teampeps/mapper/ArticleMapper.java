package fr.teampeps.mapper;

import fr.teampeps.dto.ArticleDto;
import fr.teampeps.dto.ArticleTinyDto;
import fr.teampeps.dto.ArticleTranslationDto;
import fr.teampeps.models.Article;
import fr.teampeps.models.ArticleTranslation;
import fr.teampeps.record.ArticleRequest;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ArticleMapper {

    public ArticleDto toArticleDto(Article article) {
        Map<String, ArticleTranslationDto> translationsDto = article.getTranslations().stream()
                .collect(Collectors.toMap(
                        ArticleTranslation::getLang,
                        t -> ArticleTranslationDto.builder()
                                .title(t.getTitle())
                                .content(t.getContent())
                                .build()
                ));

        return ArticleDto.builder()
                .articleType(article.getArticleType())
                .createdAt(article.getCreatedAt().toString())
                .id(article.getId())
                .thumbnailImageKey(article.getThumbnailImageKey())
                .imageKey(article.getImageKey())
                .translations(translationsDto)
                .build();
    }

    public ArticleTinyDto toArticleTinyDto(Article article) {
        Map<String, ArticleTranslationDto> translationsDto = article.getTranslations().stream()
                .collect(Collectors.toMap(
                        ArticleTranslation::getLang,
                        t -> ArticleTranslationDto.builder()
                                .title(t.getTitle())
                                .content(t.getContent().length() > 133 ? t.getContent().substring(0, 134) + "..." : t.getContent())
                                .build()
                ));

        return ArticleTinyDto.builder()
                .id(article.getId())
                .thumbnailImageKey(article.getThumbnailImageKey())
                .translations(translationsDto)
                .build();
    }

    public Article toArticle(ArticleRequest articleRequest) {
        return Article.builder()
                .articleType(articleRequest.articleType())
                .createdAt(articleRequest.createdAt())
                .id(articleRequest.id() != null ? articleRequest.id() : UUID.randomUUID().toString())
                .thumbnailImageKey(articleRequest.thumbnailImageKey())
                .imageKey(articleRequest.imageKey())
                .translations(articleRequest.translations().entrySet().stream()
                        .map(entry -> {
                            ArticleTranslation translation = new ArticleTranslation();
                            translation.setLang(entry.getKey());
                            translation.setTitle(entry.getValue().title());
                            translation.setContent(Jsoup.parse(entry.getValue().content()).text());
                            return translation;
                        })
                        .collect(Collectors.toList()))
                .build();

    }
}