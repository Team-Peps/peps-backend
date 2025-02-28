package fr.teampeps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapOfMatch {
    @Id
    @GeneratedValue
    @Column(name = "id",
            nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id",
            nullable = false)
    private Map map;

    @Column(name = "score")
    private Integer score;

    @Column(name = "opponent_score")
    private Integer opponentScore;

    @Column(name = "rounds",
            nullable = false)
    private Integer rounds;

    @ManyToOne
    @JoinColumn(name = "hero_ban_id")
    private Hero heroBan;

    @ManyToOne
    @JoinColumn(name = "opponent_hero_ban_id")
    private Hero opponentHeroBan;

}
