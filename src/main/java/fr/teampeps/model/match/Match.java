package fr.teampeps.model.match;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "matches")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @Column(name = "id",
            nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "date",
            nullable = false)
    private LocalDateTime date;

    @Column(name = "score",
            nullable = false)
    private Integer score;

    @Column(name = "opponent_score",
            nullable = false)
    private Integer opponentScore;

    @Column(name = "competition_name",
            nullable = false)
    private String competitionName;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id",
            nullable = false)
    private List<MapMatch> maps;

    @Column(name = "type",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchType type;

}
