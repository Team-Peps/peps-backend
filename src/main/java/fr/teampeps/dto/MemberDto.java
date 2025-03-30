package fr.teampeps.dto;

import fr.teampeps.model.member.MemberRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {

    private String id;
    private String pseudo;
    private String firstname;
    private String lastname;
    private String description;
    private String nationality;
    private Integer age;
    private String dateOfBirth;
    private MemberRole role;
    private Boolean isSubstitute;
    private String imageKey;
    private String xUsername;
    private String instagramUsername;
    private String tiktokUsername;
    private String youtubeUsername;
    private String twitchUsername;
}
