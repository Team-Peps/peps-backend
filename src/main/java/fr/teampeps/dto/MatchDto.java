package fr.teampeps.dto;

import fr.teampeps.enums.Game;
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
    private Integer competitionImageWidth;
    private Integer competitionImageHeight;
    private String opponent;
    private Game game;
    private String score;
    private String opponentScore;
    private String opponentImageKey;
    private Integer opponentImageWidth;
    private Integer opponentImageHeight;
    private String vodUrl;
    private String streamUrl;
}
