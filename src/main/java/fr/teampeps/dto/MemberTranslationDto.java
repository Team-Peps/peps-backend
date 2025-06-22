package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberTranslationDto {
    private String description;
}
