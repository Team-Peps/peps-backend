package fr.teampeps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "rosters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Roster {

    @Id
    @Column(name = "id",
            nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Game game;

    @Column(name = "is_opponent",
            nullable = false)
    private Boolean isOpponent;

    @Column(name = "name",
            nullable = false)
    private String name;
}
