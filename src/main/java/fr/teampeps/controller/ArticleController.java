package fr.teampeps.controller;

import fr.teampeps.dto.ArticleDto;
import fr.teampeps.dto.ArticleTinyDto;
import fr.teampeps.models.Article;
import fr.teampeps.record.ArticleRequest;
import fr.teampeps.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/article")
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    private final ArticleService articleService;
    private static final String MESSAGE_PLACEHOLDER = "message";
    private static final String ERROR_PLACEHOLDER = "error";

    @GetMapping("/all")
    public ResponseEntity<List<ArticleDto>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateArticle(
            @RequestPart("article") ArticleRequest articleRequest,
            @RequestPart(value = "imageFileThumbnail", required = false) MultipartFile imageFileThumbnail,
            @RequestPart(value = "imageFileBackground", required = false) MultipartFile imageFileBackground
    ) {
        try {
            ArticleDto updatedArticle = articleService.updateArticle(articleRequest, imageFileThumbnail, imageFileBackground);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Article mis à jour avec succès",
                    "article", updatedArticle
            ));
        } catch (Exception e) {
            log.error("❌ Error processing article with ID: {}", articleRequest.id(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors du traitement de l'article",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> saveArticle(
            @RequestPart("article") ArticleRequest articleRequest,
            @RequestPart("imageFileThumbnail") MultipartFile imageFileThumbnail,
            @RequestPart("imageFileBackground") MultipartFile imageFileBackground
    ) {
        try {
            ArticleDto updatedArticle = articleService.createArticle(articleRequest, imageFileThumbnail, imageFileBackground);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Article enregistré avec succès",
                    "article", updatedArticle
            ));
        } catch (Exception e) {
            log.error("❌ Error processing article with ID: {}", articleRequest.id(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors du traitement de l'article",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteArticle(@PathVariable String id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.ok(Map.of(MESSAGE_PLACEHOLDER, "Article supprimé avec succès"));
        } catch (Exception e) {
            log.error("❌ Error deleting article with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la suppression de l'article",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ArticleTinyDto>> getRecentArticles() {
        List<ArticleTinyDto> recentArticles = articleService.getRecentArticles();
        return ResponseEntity.ok(recentArticles);
    }

    @GetMapping
    public ResponseEntity<Page<ArticleTinyDto>> getArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String filter
    ) {
        Page<ArticleTinyDto> articles = articleService.getArticles(page, filter);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable String id) {
        ArticleDto article = articleService.getArticleById(id);
        return ResponseEntity.ok(article);
    }


}
