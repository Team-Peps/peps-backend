package fr.teampeps.dto;

import fr.teampeps.model.member.MemberRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpponentMemberDto {

    private String id;
    private String pseudo;
    private String firstname;
    private String lastname;
    private String nationality;
    private MemberRole role;
    private String roster;
}
