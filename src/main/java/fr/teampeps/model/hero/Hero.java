package fr.teampeps.model.hero;

import fr.teampeps.model.Game;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "heroes")
public class Hero {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",
            nullable = false)
    private String id;

    @Column(name = "name",
            nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id",
            nullable = false)
    private Game game;

    @Lob
    @Column(name = "image",
            nullable = false)
    private byte[] image;
}
