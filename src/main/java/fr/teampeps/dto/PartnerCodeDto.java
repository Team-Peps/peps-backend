package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PartnerCodeDto {
    private String id;
    private String code;
    private String descriptionFr;
    private String descriptionEn;
}
