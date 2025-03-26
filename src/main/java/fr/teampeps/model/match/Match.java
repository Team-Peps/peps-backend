package fr.teampeps.model.match;

import fr.teampeps.model.Roster;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "match", indexes = {
        @Index(name = "idx_match_date", columnList = "date"),
        @Index(name = "idx_match_type", columnList = "type"),
        @Index(name = "idx_match_roster", columnList = "roster_id"),
        @Index(name = "idx_match_opponent", columnList = "opponent_roster_id")
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

    @Column(name = "date",
            nullable = false)
    private LocalDateTime date;

    @Column(name = "score")
    private Integer score;

    @Column(name = "opponent_score")
    private Integer opponentScore;

    @Column(name = "competition_name",
            nullable = false)
    private String competitionName;

    @Column(name = "type",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roster_id", nullable = false)
    private Roster roster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_roster_id", nullable = false)
    private Roster opponentRoster;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<MapMatch> maps;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<TeamMatch> teamMatches;
}
