package fr.teampeps.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ambassadors")
@Cacheable
@Builder
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "ambassadorCache")
@NoArgsConstructor
@AllArgsConstructor
public class Ambassador extends TranslatableEntity<AmbassadorTranslation> {

    @Id
    @Column(name = "id",
        nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name",
        nullable = false)
    private String name;

    @Column(name = "image_key",
        nullable = false)
    private String imageKey;

    @Column(name = "twitter_x_username")
    private String twitterXUsername;

    @Column(name = "instagram_username")
    private String instagramUsername;

    @Column(name = "tiktok_username")
    private String tiktokUsername;

    @Column(name = "youtube_username")
    private String youtubeUsername;

    @Column(name = "twitch_username")
    private String twitchUsername;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AmbassadorTranslation> translations = new ArrayList<>();

}
