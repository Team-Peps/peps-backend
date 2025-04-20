package fr.teampeps.service;

import fr.teampeps.dto.ArticleDto;
import fr.teampeps.dto.ArticleTinyDto;
import fr.teampeps.mapper.ArticleMapper;
import fr.teampeps.enums.Bucket;
import fr.teampeps.models.Article;
import fr.teampeps.enums.ArticleType;
import fr.teampeps.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
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

    private Article articleGlobal;
    private ArticleDto articleDto;

    @BeforeEach
    void setUp() {
        articleGlobal = new Article();
        articleGlobal.setId("1");
        articleGlobal.setTitle("Test");
        articleGlobal.setArticleType(ArticleType.OVERWATCH);

        articleDto = ArticleDto.builder().build();
        articleDto.setId("1");
        articleDto.setTitle("Test");
    }

    @Test
    void shouldReturnAllArticles() {
        when(articleRepository.findAllOrderByCreatedAtDesc()).thenReturn(List.of(articleGlobal));
        when(articleMapper.toArticleDto(any())).thenReturn(articleDto);

        List<ArticleDto> result = articleService.getAllArticles();

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getTitle());
    }

    @Test
    void createArticle_shouldThrowBadRequest_whenImagesMissing() {
        Article article = new Article();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> articleService.createArticle(article, null, null));
        assertEquals("400 BAD_REQUEST \"Aucune image fournie\"", exception.getMessage());
    }

    @Test
    void createArticle_shouldReturnDto_whenValidInputs() {
        Article article = new Article();
        article.setTitle("Test");
        MultipartFile imageFile = mock(MultipartFile.class);
        MultipartFile thumbFile = mock(MultipartFile.class);

        when(minioService.uploadImageFromMultipartFile(imageFile, "test", Bucket.ARTICLES)).thenReturn("img.jpg");
        when(minioService.uploadImageFromMultipartFile(thumbFile, "test_thumbnail", Bucket.ARTICLES)).thenReturn("thumb.jpg");
        when(articleRepository.save(any())).thenReturn(article);
        ArticleDto dto = ArticleDto.builder().build();
        when(articleMapper.toArticleDto(any())).thenReturn(dto);

        ArticleDto result = articleService.createArticle(article, thumbFile, imageFile);
        assertEquals(dto, result);
        verify(articleRepository).save(article);
    }

    @Test
    void updateArticle_shouldThrowBadRequest_whenImagesMissing() {
        Article article = new Article();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> articleService.updateArticle(article, null, null));
        assertEquals("400 BAD_REQUEST \"Aucune image fournie\"", exception.getMessage());
    }

    @Test
    void updateArticle_shouldReturnDto_whenValidInputs() {
        Article article = new Article();
        article.setTitle("Update");
        MultipartFile imageFile = mock(MultipartFile.class);
        MultipartFile thumbFile = mock(MultipartFile.class);

        when(minioService.uploadImageFromMultipartFile(imageFile, "update", Bucket.ARTICLES)).thenReturn("img.jpg");
        when(minioService.uploadImageFromMultipartFile(thumbFile, "update_thumbnail", Bucket.ARTICLES)).thenReturn("thumb.jpg");
        when(articleRepository.save(any())).thenReturn(article);
        ArticleDto dto = ArticleDto.builder().build();
        when(articleMapper.toArticleDto(any())).thenReturn(dto);

        ArticleDto result = articleService.updateArticle(article, thumbFile, imageFile);
        assertEquals(dto, result);
        verify(articleRepository).save(article);
    }

    @Test
    void updateArticle_shouldThrowInternalError_whenUploadFails() {
        Article article = new Article();
        article.setTitle("Crash");
        MultipartFile image = mock(MultipartFile.class);
        MultipartFile thumb = mock(MultipartFile.class);

        when(minioService.uploadImageFromMultipartFile(image, "crash", Bucket.ARTICLES)).thenThrow(new RuntimeException("fail"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> articleService.updateArticle(article, thumb, image));

        assertEquals("500 INTERNAL_SERVER_ERROR \"Erreur lors de la mise à jour de l'article\"", exception.getMessage());
    }

    @Test
    void createArticle_shouldThrowInternalError_whenUploadFails() {
        Article article = new Article();
        article.setTitle("Crash");
        MultipartFile image = mock(MultipartFile.class);
        MultipartFile thumb = mock(MultipartFile.class);

        when(minioService.uploadImageFromMultipartFile(image, "crash", Bucket.ARTICLES)).thenThrow(new RuntimeException("fail"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> articleService.createArticle(article, thumb, image));

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
        when(articleRepository.findThreeRecentArticles()).thenReturn(List.of(articleGlobal));
        when(articleMapper.toArticleTinyDto(any())).thenReturn(ArticleTinyDto.builder().build());

        List<ArticleTinyDto> result = articleService.getRecentArticles();

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnPagedArticlesWithFilter() {
        Page<Article> page = new PageImpl<>(List.of(articleGlobal));
        when(articleRepository.findAllByArticleTypeIn(any(), any())).thenReturn(page);
        when(articleMapper.toArticleTinyDto(any())).thenReturn(ArticleTinyDto.builder().build());

        Page<ArticleTinyDto> result = articleService.getArticles(0, "news");

        assertEquals(1, result.getContent().size());
    }

    @Test
    void shouldReturnArticleById() {
        when(articleRepository.findById("1")).thenReturn(Optional.of(articleGlobal));
        when(articleMapper.toArticleDto(any())).thenReturn(articleDto);

        ArticleDto result = articleService.getArticleById("1");

        assertEquals("1", result.getId());
    }

    @Test
    void shouldThrowWhenArticleByIdNotFound() {
        when(articleRepository.findById("1")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> articleService.getArticleById("1"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
