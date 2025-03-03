package fr.teampeps.model.member;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.teampeps.model.Roster;
import fr.teampeps.utils.RosterDeserializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "member_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "members")
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

    @Column(name = "nationality",
            nullable = false)
    private String nationality;

    @Column(name = "role",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roster_id")
    @JsonDeserialize(using = RosterDeserializer.class)
    private Roster roster;
}
