package fr.teampeps.dto;

import fr.teampeps.enums.MemberRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberTinyDto {
    private String id;
    private String pseudo;
    private MemberRole role;
    private Boolean isSubstitute;
    private String imageKey;
}