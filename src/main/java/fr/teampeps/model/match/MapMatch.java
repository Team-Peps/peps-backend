package fr.teampeps.model.match;

import fr.teampeps.model.Hero;
import fr.teampeps.model.Map;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "map_match", indexes = {
        @Index(name = "idx_map_match_match", columnList = "match_id")
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MapMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",
            nullable = false)
    private String id;

    @Column(name = "map",
            nullable = false)
    private String map;

    @Column(name = "score",
            nullable = false)
    private Integer score;

    @Column(name = "opponent_score",
            nullable = false)
    private Integer opponentScore;

    @Column(name = "rounds",
            nullable = false)
    private Integer rounds;

    @Column(name = "hero_ban",
            nullable = false)
    private String heroBan;

    @Column(name = "opponent_hero_ban",
            nullable = false)
    private String opponentHeroBan;

    @Column(name = "game_code")
    private String gameCode;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Match match;
}
