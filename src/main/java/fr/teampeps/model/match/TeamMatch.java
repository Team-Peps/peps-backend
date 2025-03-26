package fr.teampeps.model.match;

import fr.teampeps.model.member.Member;
import fr.teampeps.model.Roster;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "team_match", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "match_id"})
}, indexes = {
        @Index(name = "idx_team_match_match", columnList = "match_id"),
        @Index(name = "idx_team_match_roster", columnList = "roster_id"),
        @Index(name = "idx_team_match_member", columnList = "member_id")
})
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TeamMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",
            nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roster_id", nullable = false)
    private Roster roster;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "is_substitute", nullable = false)
    private boolean isSubstitute;
}
