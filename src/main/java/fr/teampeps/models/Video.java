package fr.teampeps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Getter
@Setter
@Entity
@Table(
    name = "videos"
)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "videoCache")
public class Video {

    @Id
    @Column(name = "id",
            nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "link",
            nullable = false)
    private String link;

    @Column(name = "image_key")
    private String imageKey;
}
