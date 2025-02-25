package fr.teampeps.dto;

import fr.teampeps.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    private String id;
    private String pseudo;
    private String firstname;
    private String lastname;
    private String dateOfBirth;
    private Integer age;
    private Integer dpi;
    private String nationality;
    private Role role;

}
