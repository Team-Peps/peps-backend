package fr.teampeps.dto;

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
    private List<String> codes;
    private Boolean isActive;
    private Long order;
}
