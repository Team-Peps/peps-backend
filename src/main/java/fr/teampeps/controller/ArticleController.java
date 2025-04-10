package fr.teampeps.controller;

import fr.teampeps.dto.ArticleDto;
import fr.teampeps.dto.ArticleTinyDto;
import fr.teampeps.model.article.Article;
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
@RequestMapping("/api/v1/article")
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/all")
    public ResponseEntity<List<ArticleDto>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateArticle(
            @RequestPart("article") Article article,
            @RequestPart(value = "imageFileThumbnail", required = false) MultipartFile imageFileThumbnail,
            @RequestPart(value = "imageFileBackground", required = false) MultipartFile imageFileBackground
    ) {
        try {
            ArticleDto updatedArticle = articleService.updateArticle(article, imageFileThumbnail, imageFileBackground);
            return ResponseEntity.ok(Map.of(
                    "message", "Article mis à jour avec succès",
                    "article", updatedArticle
            ));
        } catch (Exception e) {
            log.error("❌ Error processing article with ID: {}", article.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors du traitement de l'article",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> saveArticle(
            @RequestPart("article") Article article,
            @RequestPart("imageFileThumbnail") MultipartFile imageFileThumbnail,
            @RequestPart("imageFileBackground") MultipartFile imageFileBackground
    ) {
        log.info("Bg: {}", imageFileBackground.getOriginalFilename());
        log.info("Th: {}", imageFileThumbnail.getOriginalFilename());
        try {
            ArticleDto updatedArticle = articleService.createArticle(article, imageFileThumbnail, imageFileBackground);
            return ResponseEntity.ok(Map.of(
                    "message", "Article enregistré avec succès",
                    "article", updatedArticle
            ));
        } catch (Exception e) {
            log.error("❌ Error processing article with ID: {}", article.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors du traitement de l'article",
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteArticle(@PathVariable String id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.ok(Map.of("message", "Article supprimé avec succès"));
        } catch (Exception e) {
            log.error("❌ Error deleting article with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors de la suppression de l'article",
                    "error", e.getMessage()
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
