package fr.teampeps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Data
@Builder
@Entity
@Table(name = "images")
@NoArgsConstructor
@AllArgsConstructor
public class Image {
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
    private String type;

    @Lob
    @Column(name = "imageData",
            nullable = false)
    private byte[] imageData;
}
