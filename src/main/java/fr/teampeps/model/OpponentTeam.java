package fr.teampeps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "opponent_teams")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpponentTeam {

    @Id
    @Column(name = "id",
            nullable = false)
    @GeneratedValue
    private String id;

    @Column(name = "name",
            nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id",
            nullable = false)
    private Image image;
}
