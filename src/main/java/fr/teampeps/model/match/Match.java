package fr.teampeps.model.match;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;

@Table(name = "match", indexes = {
        @Index(name = "idx_match_datetime", columnList = "datetime"),
        @Index(name = "idx_match_competitionName", columnList = "competition_name")
})
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",
            nullable = false)
    private String id;

    @Column(name = "datetime",
            nullable = false)
    private LocalDateTime datetime;

    @Column(name = "score")
    private String score;

    @Column(name = "opponent_score")
    private String opponentScore;

    @Column(name = "competition_name",
            nullable = false)
    private String competitionName;

    @Column(name = "opponent",
            nullable = false)
    private String opponent;

    @Column(name = "vod_url",
            nullable = false)
    private String vodUrl;

    @Column(name = "stream_url",
            nullable = false)
    private String streamUrl;

    @Column(name = "game",
            nullable = false)
    private String game;
}
