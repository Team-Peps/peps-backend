package fr.teampeps.service;

import fr.teampeps.dto.ArticleDto;
import fr.teampeps.dto.ArticleTinyDto;
import fr.teampeps.mapper.ArticleMapper;
import fr.teampeps.model.Bucket;
import fr.teampeps.model.article.Article;
import fr.teampeps.model.article.ArticleType;
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

    public ArticleDto createArticle(Article article, MultipartFile thumbnailImageFile, MultipartFile imageFile) {

        if(imageFile == null || thumbnailImageFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucune image fournie");
        }

        try {
            String fileName = article.getTitle().toLowerCase();
            String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, fileName, Bucket.ARTICLES);
            article.setImageKey(imageUrl);

            String fileNameThumbnail = article.getTitle().toLowerCase() + "_thumbnail";
            String thumbnailImageUrl = minioService.uploadImageFromMultipartFile(thumbnailImageFile, fileNameThumbnail, Bucket.ARTICLES);
            article.setThumbnailImageKey(thumbnailImageUrl);

            return articleMapper.toArticleDto(articleRepository.save(article));

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de l'article", e);
        }
    }

    public ArticleDto updateArticle(Article article, MultipartFile thumbnailImageFile, MultipartFile imageFile) {

       if(imageFile == null || thumbnailImageFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucune image fournie");
        }

       try {
            String fileName = article.getTitle().toLowerCase();
            String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, fileName, Bucket.ARTICLES);
            article.setImageKey(imageUrl);

            String fileNameThumbnail = article.getTitle().toLowerCase() + "_thumbnail";
            String thumbnailImageUrl = minioService.uploadImageFromMultipartFile(thumbnailImageFile, fileNameThumbnail, Bucket.ARTICLES);
            article.setThumbnailImageKey(thumbnailImageUrl);

            return articleMapper.toArticleDto(articleRepository.save(article));

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
