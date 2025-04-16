package fr.teampeps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTinyDto {
    private String id;
    private String username;
    private String email;
    private String discordId;
    private String avatarUrl;
}
