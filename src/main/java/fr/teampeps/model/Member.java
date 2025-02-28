package fr.teampeps.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.teampeps.utils.RosterDeserializer;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

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
    @JoinColumn(name = "roster_id")
    @JsonDeserialize(using = RosterDeserializer.class)
    private Roster roster;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id",
            nullable = true)
    private Image image;

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", pseudo='" + pseudo + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", role=" + role +
                ", nationality=" + nationality +
                ", dpi=" + dpi + '}';
    }
}
