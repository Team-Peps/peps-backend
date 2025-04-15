package fr.teampeps.model;


import fr.teampeps.model.member.Member;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Getter
@Setter
@Entity
@Table(
        name = "achievements",
        indexes = {
                @Index(name = "idx_achievement_game", columnList = "game"),
        }
)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "achievementCache")
public class Achievement {

    @Id
    @Column(name = "id",
            nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "competition_name",
            nullable = false)
    private String competitionName;

    @Column(name = "ranking",
            nullable = false)
    private Integer ranking;

    @Column(name = "game")
    @Enumerated(EnumType.STRING)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Member member;

}
