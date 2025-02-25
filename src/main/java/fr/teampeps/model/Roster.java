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
    @GeneratedValue
    private Long id;

    @Column(name = "name",
            nullable = false)
    private String name = "Team Peps";

    @Column(name = "game",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private Game game;

    @OneToMany(mappedBy = "roster",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<Member> members;
}
