package fr.teampeps.model;

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
    @GeneratedValue
    @Column(name = "id",
            nullable = false)
    private Long id;

    @Column(name = "name",
            nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id",
            nullable = false)
    private Game game;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id",
            nullable = false)
    private Image image;
}
