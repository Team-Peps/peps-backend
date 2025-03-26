package fr.teampeps.model.record;

public record Round(
        String mapId,
        String gameCode,
        String bannedHeroPeps,
        String bannedHeroOpponent,
        Integer scorePeps,
        Integer scoreOpponent,
        Integer round
) {
}
