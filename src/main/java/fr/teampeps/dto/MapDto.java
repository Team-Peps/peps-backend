package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MapDto {
    String id;
    String name;
    String type;
    String imageKey;
}
