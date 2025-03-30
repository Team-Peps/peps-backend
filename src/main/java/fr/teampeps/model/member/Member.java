package fr.teampeps.model.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "members")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "memberCache")
public class Member {

    @Id
    @Column(name = "id",
            nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "pseudo",
            nullable = false,
            unique = true)
    private String pseudo;

    @Column(name = "firstname",
            nullable = false)
    private String firstname;

    @Column(name = "lastname",
            nullable = false)
    private String lastname;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_of_birth",
            nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "nationality",
            nullable = false)
    private String nationality;

    @Column(name = "role",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "is_substitute")
    private Boolean isSubstitute;

    @Column(name = "image_key")
    private String imageKey;

    @Column(name = "x_username")
    private String xUsername;

    @Column(name = "instagram_username")
    private String instagramUsername;

    @Column(name = "tiktok_username")
    private String tiktokUsername;

    @Column(name = "youtube_username")
    private String youtubeUsername;

    @Column(name = "twitch_username")
    private String twitchUsername;
}
