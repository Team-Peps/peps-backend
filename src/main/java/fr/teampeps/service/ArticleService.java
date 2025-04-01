package fr.teampeps.service;

import fr.teampeps.dto.ArticleDto;
import fr.teampeps.mapper.ArticleMapper;
import fr.teampeps.model.Bucket;
import fr.teampeps.model.article.Article;
import fr.teampeps.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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

    public ArticleDto createArticle(Article article, MultipartFile imageFile) {
        try {
            if (imageFile != null) {
                String fileName = article.getTitle().toLowerCase();
                String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, fileName, Bucket.ARTICLES);
                article.setImageKey(imageUrl);
            }

            article.setCreatedAt(LocalDate.now());
            return articleMapper.toArticleDto(articleRepository.save(article));

        } catch (Exception e) {
            throw new RuntimeException("Error creating article", e);
        }
    }

    public ArticleDto updateArticle(Article article, MultipartFile imageFile) {
        log.info(article.toString());
        try {
            if (imageFile != null) {
                String fileName = article.getTitle().toLowerCase();
                String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, fileName, Bucket.ARTICLES);
                article.setImageKey(imageUrl);
            }

            return articleMapper.toArticleDto(articleRepository.save(article));

        } catch (Exception e) {
            throw new RuntimeException("Error updating article with ID: " + article.getId(), e);
        }
    }

    public void deleteArticle(String id) {
        try {
            articleRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article non trouvé", e);
        }
    }
}
