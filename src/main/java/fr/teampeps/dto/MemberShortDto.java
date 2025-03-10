package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberShortDto {
    private String id;
    private String pseudo;
}
