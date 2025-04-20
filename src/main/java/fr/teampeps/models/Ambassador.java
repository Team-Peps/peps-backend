package fr.teampeps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Getter
@Setter
@Entity
@Table(name = "ambassadors")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "ambassadorCache")
public class Ambassador {

    @Id
    @Column(name = "id",
        nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name",
        nullable = false)
    private String name;

    @Column(name = "description",
        nullable = false,
        columnDefinition = "TEXT")
    private String description;

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

}
