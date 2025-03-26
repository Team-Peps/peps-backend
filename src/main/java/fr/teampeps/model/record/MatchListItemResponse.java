package fr.teampeps.model.record;

import java.time.LocalDateTime;

public record MatchListItemResponse(
        String id,
        LocalDateTime date,
        String competitionName,
        String type,
        String roster,
        String opponentRoster,
        String game,
        Integer score,
        Integer opponentScore
) {}
