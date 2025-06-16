package fr.teampeps.service;

import fr.teampeps.dto.ArticleDto;
import fr.teampeps.dto.ArticleTinyDto;
import fr.teampeps.mapper.ArticleMapper;
import fr.teampeps.enums.Bucket;
import fr.teampeps.models.Article;
import fr.teampeps.enums.ArticleType;
import fr.teampeps.models.ArticleTranslation;
import fr.teampeps.record.ArticleRequest;
import fr.teampeps.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MinioService minioService;
    private final ArticleMapper articleMapper;

    public List<ArticleDto> getAllArticles() {
        return articleRepository.findAllOrderByCreatedAtDesc().stream()
                .map(articleMapper::toArticleDto)
                .toList();
    }

    public ArticleDto createArticle(
            ArticleRequest articleRequest,
            MultipartFile thumbnailImageFile,
            MultipartFile imageFile
    ) {

        if(imageFile == null || thumbnailImageFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucune image fournie");
        }

        Article article = articleMapper.toArticle(articleRequest);

        try {
            String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, article.getId(), Bucket.ARTICLES);
            String thumbnailImageUrl = minioService.uploadImageFromMultipartFile(thumbnailImageFile, article.getId() + "_thumbnail", Bucket.ARTICLES);

            article.setThumbnailImageKey(thumbnailImageUrl);
            article.setImageKey(imageUrl);

            List<ArticleTranslation> validTranslations = article.getTranslations().stream()
                .filter(t -> t.getLang() != null && !t.getLang().isBlank() && t.getTitle() != null && !t.getTitle().isBlank() && t.getContent() != null && !t.getContent().isBlank())
                .peek(articleTranslation -> {
                    articleTranslation.setParent(article);
                })
                .toList();

            article.setTranslations(validTranslations);

            return articleMapper.toArticleDto(articleRepository.save(article));

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de l'article", e);
        }
    }

    public ArticleDto updateArticle(
            ArticleRequest articleRequest,
            MultipartFile thumbnailImageFile,
            MultipartFile imageFile
    ) {
        Article existingArticle = articleRepository.findById(articleRequest.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));
        existingArticle.setArticleType(articleRequest.articleType());

        Map<String, ArticleTranslation> translationsByLang = existingArticle.getTranslations().stream()
                .collect(Collectors.toMap(ArticleTranslation::getLang, Function.identity()));

        articleRequest.translations().forEach((lang, tRequest) -> {
            ArticleTranslation translation = translationsByLang.get(lang.toLowerCase());
            if(translation != null) {
                translation.setContent(tRequest.content());
                translation.setTitle(tRequest.title());
            }
        });

       try {
           if(imageFile != null) {
                String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, existingArticle.getId(), Bucket.ARTICLES);
                existingArticle.setImageKey(imageUrl);
            }

           if(thumbnailImageFile != null) {
               String thumbnailImageUrl = minioService.uploadImageFromMultipartFile(thumbnailImageFile, existingArticle.getId() + "_thumbnail", Bucket.ARTICLES);
               existingArticle.setThumbnailImageKey(thumbnailImageUrl);
           }

            return articleMapper.toArticleDto(articleRepository.save(existingArticle));

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour de l'article", e);
        }
    }

    public void deleteArticle(String id) {
        try {
            articleRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé", e);
        }
    }

    public List<ArticleTinyDto> getRecentArticles() {
        return articleRepository.findThreeRecentArticles().stream()
                .map(articleMapper::toArticleTinyDto)
                .toList();
    }

    public Page<ArticleTinyDto> getArticles(int page, String filter) {
        Pageable pageable = PageRequest.of(page, 9, Sort.by("createdAt").descending());

        List<ArticleType> types = Arrays.stream(filter.split(","))
                .map(String::toUpperCase)
                .filter(ArticleType::contains)
                .map(ArticleType::valueOf)
                .toList();

        return articleRepository.findAllByArticleTypeIn(types, pageable)
                .map(articleMapper::toArticleTinyDto);
    }

    public ArticleDto getArticleById(String id) {
        return articleRepository.findById(id)
                .map(articleMapper::toArticleDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé"));
    }
}
