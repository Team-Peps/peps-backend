package fr.teampeps.model.heroe;

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
public class Heroe {

    @Id
    @Column(name = "id",
            nullable = false)
    private String id;

    @Column(name = "name",
            nullable = false)
    private String name;

    @Column(name = "game",
            nullable = false)
    private String game;

    @Column(name = "role",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private HeroeRole role;

    @Column(name = "image_key",
            nullable = false)
    private String imageKey;
}
