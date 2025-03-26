package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RosterTinyDto {
    private String id;
    private String name;
    private String game;
}
