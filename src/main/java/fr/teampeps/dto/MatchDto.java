package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchDto {
    private String id;
    private String datetime;
    private String competitionName;
    private String opponent;
    private String game;
    private String score;
    private String opponentScore;
    private String vodUrl;
    private String streamUrl;
}
