package fr.teampeps.model;

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
    @GeneratedValue
    @Column(name = "id",
            nullable = false)
    private Long id;

    @Column(name = "name",
            nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id",
            nullable = false)
    private Image image;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_type_id",
            nullable = false)
    private MapType mapType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id",
            nullable = false)
    private Game game;
}
