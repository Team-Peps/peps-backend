package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class PartnerDto {
    private String id;
    private String name;
    private String imageKey;
    private String link;
    private List<PartnerCodeDto> codes;
    private Boolean isActive;
    private Long order;
    private String type;
    private Map<String, PartnerTranslationDto> translations;
}
