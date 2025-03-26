package fr.teampeps.model.record;

import fr.teampeps.model.match.MatchType;

public record MatchCreateRequest(
        String date,
        String competitionName,
        MatchType type,
        String roster,
        String opponentRoster
) {}
