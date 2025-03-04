package fr.teampeps.model.map;

import fr.teampeps.model.Game;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "maps")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Map {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",
            nullable = false)
    private String id;

    @Column(name = "name",
            nullable = false)
    private String name;

    @Lob
    @Column(name = "image",
            nullable = false)
    private byte[] image;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_type_id",
            nullable = false)
    private MapType mapType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id",
            nullable = false)
    private Game game;
}
