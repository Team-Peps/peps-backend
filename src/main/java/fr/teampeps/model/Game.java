package fr.teampeps.model;

import fr.teampeps.model.map.Map;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "games")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @Column(name = "id",
            nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name",
            nullable = false)
    private String name;

    @OneToMany(mappedBy = "game",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<Map> maps;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id",
            nullable = true)
    private Image image;
}
