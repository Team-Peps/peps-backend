package fr.teampeps.record;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record AmbassadorRequest(
    String id,
    @NotBlank(message = "Le nom de l'ambassadeur ne peut pas être vide")
    String name,
    String imageKey,
    String twitterXUsername,
    String instagramUsername,
    String tiktokUsername,
    String youtubeUsername,
    String twitchUsername,
    Map<String, AmbassadorTranslationRequest> translations
) {
}
