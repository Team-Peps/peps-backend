package fr.teampeps.mapper;

import fr.teampeps.dto.MatchDto;
import fr.teampeps.model.match.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchMapper {

    public MatchDto toMatchDto(Match match) {
        return MatchDto.builder()
                .id(match.getId())
                .datetime(String.valueOf(match.getDatetime()))
                .competitionName(match.getCompetitionName())
                .opponent(match.getOpponent())
                .game(match.getGame())
                .score(match.getScore())
                .opponentScore(match.getOpponentScore())
                .vodUrl(match.getVodUrl())
                .streamUrl(match.getStreamUrl())
                .build();
    }
}
