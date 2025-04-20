package fr.teampeps.dto;

import fr.teampeps.enums.Game;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AchievementDto {
    private String id;
    private String competitionName;
    private Integer ranking;
    private Game game;
}
