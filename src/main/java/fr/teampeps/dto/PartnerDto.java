package fr.teampeps.dto;

import fr.teampeps.models.PartnerCode;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PartnerDto {
    private String id;
    private String name;
    private String description;
    private String imageKey;
    private String link;
    private List<PartnerCode> codes;
    private Boolean isActive;
    private Long order;
    private String type;
}
