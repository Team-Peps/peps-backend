package fr.teampeps.models;

import fr.teampeps.enums.Game;
import fr.teampeps.enums.HeroeRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "heroes",
        indexes = {
                @Index(name = "idx_heroe_game", columnList = "game"),
                @Index(name = "idx_heroe_role", columnList = "role")
        }
)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "heroeCache")
public class Heroe {

    @Id
    @Column(name = "id",
            nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name",
            nullable = false)
    private String name;

    @Column(name = "game",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private Game game;

    @Column(name = "role",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private HeroeRole role;

    @Column(name = "image_key",
            nullable = false)
    private String imageKey;
}
