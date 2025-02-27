package fr.teampeps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "map_types")
public class MapType {

    @Id
    @GeneratedValue
    @Column(name = "id",
            nullable = false)
    private Long id;

    @Column(name = "name",
            nullable = false)
    private String name;
}
