package fr.teampeps.model.map;

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

    @Column(name = "type",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private MapType type;

    @Column(name = "game",
            nullable = false)
    private String game;

    @Column(name = "image_key",
            nullable = false)
    private String imageKey;
}
