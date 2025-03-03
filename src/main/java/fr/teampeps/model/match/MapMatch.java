package fr.teampeps.model.match;

import fr.teampeps.model.hero.Hero;
import fr.teampeps.model.map.Map;
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
public class MapMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",
            nullable = false)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id",
            nullable = false)
    private Map map;

    @Column(name = "score",
            nullable = false)
    private Integer score;

    @Column(name = "opponent_score",
            nullable = false)
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

    @Column(name = "game_code")
    private String gameCode;

}
