package fr.teampeps.repository;

import fr.teampeps.model.article.Article;
import fr.teampeps.model.article.ArticleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, String> {

    @Query("SELECT a FROM Article a ORDER BY a.createdAt DESC")
    List<Article> findAllOrderByCreatedAtDesc();

    @Query("SELECT a FROM Article a ORDER BY a.createdAt DESC LIMIT 3")
    List<Article> findThreeRecentArticles();

    Page<Article> findAllByArticleTypeIn(List<ArticleType> types, Pageable pageable);
}
