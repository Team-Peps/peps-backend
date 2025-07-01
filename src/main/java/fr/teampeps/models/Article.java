package fr.teampeps.models;

import fr.teampeps.enums.ArticleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "articles",
        indexes = {
                @Index(name = "idx_article_created_at", columnList = "created_at"),
                @Index(name = "idx_article_type", columnList = "article_type")
        }
)
@Cacheable
@Builder
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "articleCache")
@NoArgsConstructor
@AllArgsConstructor
public class Article extends TranslatableEntity<ArticleTranslation> {

    @Id
    @Column(name = "id",
            nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "thumbnail_image_key")
    private String thumbnailImageKey;

    @Column(name = "image_key")
    private String imageKey;

    @CreationTimestamp
    @Column(name = "created_at",
            nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "article_type",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private ArticleType articleType;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleTranslation> translations = new ArrayList<>();
}
