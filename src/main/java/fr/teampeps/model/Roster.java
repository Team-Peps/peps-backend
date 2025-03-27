package fr.teampeps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(
        name = "rosters",
        uniqueConstraints = @UniqueConstraint(columnNames = {"game", "name_lower"}, name = "rosters_unique")
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "rosterCache")
public class Roster {

    @Id
    @Column(name = "id",
            nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "game",
            nullable = false)
    private String game;

    @Column(name = "is_opponent",
            nullable = false)
    private Boolean isOpponent;

    @Column(name = "name",
            nullable = false)
    private String name;

    @Column(name = "name_lower",
            nullable = false)
    private String nameLower;

    @Column(name = "image_key")
    private String imageKey;
}
