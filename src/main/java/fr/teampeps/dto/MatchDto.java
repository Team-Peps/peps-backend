package fr.teampeps.dto;

import fr.teampeps.model.Game;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MatchDto {
    private String id;
    private LocalDateTime datetime;
    private String competitionName;
    private String competitionImageKey;
    private String opponent;
    private Game game;
    private String score;
    private String opponentScore;
    private String opponentImageKey;
    private String vodUrl;
    private String streamUrl;
}
