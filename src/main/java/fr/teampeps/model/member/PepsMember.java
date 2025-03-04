package fr.teampeps.model.member;

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

    @Lob
    @Column(name = "image",
            nullable = false)
    private byte[] image;

}
