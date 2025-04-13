package fr.teampeps.model;


import fr.teampeps.model.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Getter
@Setter
@Entity
@Table(name = "achievements")
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

    @Column(name = "game",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
