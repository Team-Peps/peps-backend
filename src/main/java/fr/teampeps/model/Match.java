package fr.teampeps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    @GeneratedValue
    private Long id;

    @Column(name = "date",
            nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_team_id",
            nullable = false)
    private OpponentTeam opponent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roster_id",
            nullable = false)
    private Roster roster;

    @Column(name = "score",
            nullable = false)
    private Integer score;

    @Column(name = "opponent_score",
            nullable = false)
    private Integer opponentScore;

    @Column(name = "competition_name",
            nullable = false)
    private String competitionName;
}
