package fr.teampeps.model.record;

import fr.teampeps.model.match.MatchType;

import java.util.Set;

public record MatchCreateFinishedRequest(
        String datetime,
        String competitionName,
        MatchType type,
        String roster,
        String opponentRoster,
        String game,
        Set<String> opponentPlayers,
        Set<String> pepsPlayers,
        Integer score,
        Integer opponentScore,
        Set<Round> rounds
) {}


