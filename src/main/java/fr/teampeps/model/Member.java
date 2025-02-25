package fr.teampeps.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "members")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @Column(name = "id",
            nullable = false)
    @GeneratedValue
    private Long id;

    @Column(name = "pseudo",
            nullable = false,
            unique = true)
    private String pseudo;

    @Column(name = "firstname",
            nullable = false)
    private String firstname;

    @Column(name = "lastname",
            nullable = false)
    private String lastname;

    @Column(name = "date_of_birth",
            nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "dpi")
    private Integer dpi;

    @Column(name = "nationality",
            nullable = false)
    private String nationality;

    @Column(name = "role",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "roster_id",
            nullable = false)
    private Roster roster;

}
