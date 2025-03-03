package fr.teampeps.model.member;

import fr.teampeps.model.Image;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@ToString
@DiscriminatorValue("PEPS")
public class PepsMember extends Member {

    @Column(name = "date_of_birth",
            nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "dpi")
    private Integer dpi;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id",
            nullable = true)
    private Image image;

}
