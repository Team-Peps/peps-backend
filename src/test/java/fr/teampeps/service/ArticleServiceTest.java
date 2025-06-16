package fr.teampeps.service;

import fr.teampeps.dto.ArticleDto;
import fr.teampeps.dto.ArticleTinyDto;
import fr.teampeps.dto.ArticleTranslationDto;
import fr.teampeps.mapper.ArticleMapper;
import fr.teampeps.enums.Bucket;
import fr.teampeps.models.Article;
import fr.teampeps.enums.ArticleType;
import fr.teampeps.models.ArticleTranslation;
import fr.teampeps.record.ArticleRequest;
import fr.teampeps.record.ArticleTranslationRequest;
import fr.teampeps.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock private ArticleRepository articleRepository;
    @Mock private MinioService minioService;
    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleService articleService;

    private Article article;
    private ArticleRequest articleRequest;
    private ArticleDto articleDto;

    @BeforeEach
    void setUp() {

        article = new Article();
        article.setId("articleId");

        articleRequest = new ArticleRequest(
                "articleId",
                "image_thumbnail.jpg",
                "image.jpg",
                null,
                ArticleType.OVERWATCH,
                Map.of(
                        "fr", new ArticleTranslationRequest("Titre", "Contenu"),
                        "en", new ArticleTranslationRequest("Title", "Content")
                )
        );

        articleDto = ArticleDto.builder().build();
        articleDto.setId("articleId");
        articleDto.setTranslations(
                Map.of(
                        "fr", ArticleTranslationDto
                                .builder()
                                .title("Titre")
                                .content("Contenu")
                                .build(),
                        "en", ArticleTranslationDto
                                .builder()
                                .title("Title")
                                .content("Content")
                                .build()
                )
        );
    }

    @Test
    void shouldReturnAllArticles() {
        when(articleRepository.findAllOrderByCreatedAtDesc()).thenReturn(List.of(article));
        when(articleMapper.toArticleDto(any())).thenReturn(articleDto);

        List<ArticleDto> result = articleService.getAllArticles();

        assertEquals(1, result.size());
        assertEquals("articleId", result.get(0).getId());
    }

    @Test
    void createArticle_shouldThrowBadRequest_whenImagesMissing() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> articleService.createArticle(articleRequest, null, null));
        assertEquals("400 BAD_REQUEST \"Aucune image fournie\"", exception.getMessage());
    }

    @Test
    void createArticle_shouldCreateArticleSuccessfully() throws Exception {
        // Given
        MultipartFile imageFile = mock(MultipartFile.class);
        MultipartFile thumbnailImageFile = mock(MultipartFile.class);

        // L'article généré depuis le mapper
        when(articleMapper.toArticle(articleRequest)).thenReturn(article);

        // Simuler le comportement de minioService
        when(minioService.uploadImageFromMultipartFile(imageFile, "articleId", Bucket.ARTICLES))
                .thenReturn("image.jpg");
        when(minioService.uploadImageFromMultipartFile(thumbnailImageFile, "articleId_thumbnail", Bucket.ARTICLES))
                .thenReturn("image_thumbnail.jpg");

        // Mock le repository save
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        // Simuler la conversion finale
        when(articleMapper.toArticleDto(article)).thenReturn(articleDto);

        // When
        ArticleDto result = articleService.createArticle(articleRequest, thumbnailImageFile, imageFile);

        // Then
        assertEquals("articleId", result.getId());
        verify(articleMapper).toArticle(articleRequest);
        verify(minioService).uploadImageFromMultipartFile(imageFile, "articleId", Bucket.ARTICLES);
        verify(minioService).uploadImageFromMultipartFile(thumbnailImageFile, "articleId_thumbnail", Bucket.ARTICLES);
        verify(articleRepository).save(article);
        verify(articleMapper).toArticleDto(article);
    }


    @Test
    void updateArticle_shouldUpdateArticleSuccessfully() throws Exception {
        // Given
        MultipartFile imageFile = mock(MultipartFile.class);
        MultipartFile thumbnailImageFile = mock(MultipartFile.class);

        ArticleTranslation translationFr = new ArticleTranslation();
        translationFr.setLang("fr");
        translationFr.setTitle("Ancien titre FR");
        translationFr.setContent("Ancien contenu FR");

        ArticleTranslation translationEn = new ArticleTranslation();
        translationEn.setLang("en");
        translationEn.setTitle("Old title EN");
        translationEn.setContent("Old content EN");

        article.setTranslations(List.of(translationFr, translationEn));

        when(articleRepository.findById("articleId")).thenReturn(Optional.of(article));

        when(minioService.uploadImageFromMultipartFile(imageFile, "articleId", Bucket.ARTICLES))
                .thenReturn("new_image.jpg");
        when(minioService.uploadImageFromMultipartFile(thumbnailImageFile, "articleId_thumbnail", Bucket.ARTICLES))
                .thenReturn("new_thumbnail.jpg");

        when(articleRepository.save(any(Article.class))).thenReturn(article);
        when(articleMapper.toArticleDto(article)).thenReturn(articleDto);

        // When
        ArticleDto result = articleService.updateArticle(articleRequest, thumbnailImageFile, imageFile);

        // Then
        assertEquals("articleId", result.getId());
        assertEquals("Titre", translationFr.getTitle());
        assertEquals("Contenu", translationFr.getContent());
        assertEquals("Title", translationEn.getTitle());
        assertEquals("Content", translationEn.getContent());

        verify(minioService).uploadImageFromMultipartFile(imageFile, "articleId", Bucket.ARTICLES);
        verify(minioService).uploadImageFromMultipartFile(thumbnailImageFile, "articleId_thumbnail", Bucket.ARTICLES);
        verify(articleRepository).save(article);
        verify(articleMapper).toArticleDto(article);
    }

    @Test
    void createArticle_shouldThrowInternalError_whenUploadFails() {
        Article article = new Article();
        article.setId("articleId");
        MultipartFile image = mock(MultipartFile.class);
        MultipartFile thumb = mock(MultipartFile.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> articleService.createArticle(articleRequest, thumb, image));

        assertEquals("500 INTERNAL_SERVER_ERROR \"Erreur lors de la création de l'article\"", exception.getMessage());
    }

    @Test
    void shouldDeleteArticle() {
        assertDoesNotThrow(() -> articleService.deleteArticle("1"));
        verify(articleRepository).deleteById("1");
    }

    @Test
    void shouldThrowWhenArticleNotFoundOnDelete() {
        doThrow(EntityNotFoundException.class).when(articleRepository).deleteById("1");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> articleService.deleteArticle("1"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void shouldReturnRecentArticles() {
        when(articleRepository.findThreeRecentArticles()).thenReturn(List.of(article));
        when(articleMapper.toArticleTinyDto(any())).thenReturn(ArticleTinyDto.builder().build());

        List<ArticleTinyDto> result = articleService.getRecentArticles();

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnPagedArticlesWithFilter() {
        Page<Article> page = new PageImpl<>(List.of(article));
        when(articleRepository.findAllByArticleTypeIn(any(), any())).thenReturn(page);
        when(articleMapper.toArticleTinyDto(any())).thenReturn(ArticleTinyDto.builder().build());

        Page<ArticleTinyDto> result = articleService.getArticles(0, "news");

        assertEquals(1, result.getContent().size());
    }

    @Test
    void shouldReturnArticleById() {
        when(articleRepository.findById("1")).thenReturn(Optional.of(article));
        when(articleMapper.toArticleDto(any())).thenReturn(articleDto);

        ArticleDto result = articleService.getArticleById("1");

        assertEquals("articleId", result.getId());
    }

    @Test
    void shouldThrowWhenArticleByIdNotFound() {
        when(articleRepository.findById("1")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> articleService.getArticleById("1"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
