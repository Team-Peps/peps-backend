package fr.teampeps.model.member;

import fr.teampeps.model.Achievement;
import fr.teampeps.model.Game;
import fr.teampeps.model.heroe.Heroe;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private Boolean isSubstitute = true;

    @Column(name = "is_active")
    private Boolean isActive = true;

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

    @Column(name = "game",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private Game game;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Achievement> achievements = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "member_favorite_heroes",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "hero_id")
    )
    private List<Heroe> favoriteHeroes = new ArrayList<>();

}
