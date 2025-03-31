package fr.teampeps.model.article;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "articles")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "articleCache")
public class Article {

    @Id
    @Column(name = "id",
            nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "title",
            nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_key")
    private String imageKey;

    @Column(name = "created_at",
            nullable = false)
    private LocalDate createdAt;

    @Column(name = "article_type",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private ArticleType articleType;
}
