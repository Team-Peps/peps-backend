package fr.teampeps.dto;

import fr.teampeps.model.Game;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeroeDto {
    private String id;
    private String name;
    private String role;
    private String imageKey;
    private Game game;
}
