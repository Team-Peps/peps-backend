package fr.teampeps.record;

import fr.teampeps.enums.Game;
import fr.teampeps.enums.MemberRole;
import fr.teampeps.models.Heroe;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record MemberRequest(
    String id,
    String pseudo,
    String firstname,
    String lastname,
    LocalDate dateOfBirth,
    String nationality,
    MemberRole role,
    Boolean isSubstitute,
    Boolean isActive,
    String xUsername,
    String instagramUsername,
    String tiktokUsername,
    String youtubeUsername,
    String twitchUsername,
    Game game,
    List<Heroe> favoriteHeroes,
    Map<String, MemberTranslationRequest> translations
) {
}
