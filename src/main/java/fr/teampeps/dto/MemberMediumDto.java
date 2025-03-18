package fr.teampeps.dto;

import fr.teampeps.model.member.MemberRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberMediumDto {
    private String pseudo;
    private String firstname;
    private String lastname;
    private MemberRole role;

    public MemberMediumDto(String pseudo, String firstname, String lastname, MemberRole role) {
        this.pseudo = pseudo;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }
}
