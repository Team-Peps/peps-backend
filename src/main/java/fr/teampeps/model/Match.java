package fr.teampeps.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;

@Table(name = "matchs", indexes = {
    @Index(name = "idx_match_game", columnList = "game"),
    @Index(name = "idx_match_score", columnList = "score")
})
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "matchCache")
public class Match {

    @Id
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

    @Column(name = "competition_image_key",
            nullable = false)
    private String competitionImageKey;

    @Column(name = "opponent",
            nullable = false)
    private String opponent;

    @Column(name = "opponent_image_key",
            nullable = false)
    private String opponentImageKey;

    @Column(name = "vod_url")
    private String vodUrl;

    @Column(name = "stream_url")
    private String streamUrl;

    @Column(name = "game",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private Game game;
}
