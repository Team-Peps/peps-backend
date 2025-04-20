package fr.teampeps.dto;

import fr.teampeps.enums.AuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String id;
    private String username;
    private String email;
    private List<String> authorities;
    private Boolean isEnable;
    private String discordId;
    private AuthType authType;
    private String createdAt;
}
