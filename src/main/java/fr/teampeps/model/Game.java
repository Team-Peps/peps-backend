package fr.teampeps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @GeneratedValue
    private String id;

    @Column(name = "name",
            nullable = false)
    private String name;
}
