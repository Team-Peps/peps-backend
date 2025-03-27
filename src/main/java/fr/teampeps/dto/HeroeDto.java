package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeroeDto {
    String id;
    String name;
    String role;
    String imageKey;
}
