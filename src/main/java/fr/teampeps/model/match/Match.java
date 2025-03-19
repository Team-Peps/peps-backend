package fr.teampeps.model.match;

import fr.teampeps.model.Roster;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "matches")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Roster team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_team_id", nullable = false)
    private Roster opponentTeam;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private Set<TeamMatch> teamMatches = new HashSet<>();
}
