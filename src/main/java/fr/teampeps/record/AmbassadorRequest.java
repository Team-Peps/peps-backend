package fr.teampeps.record;

import java.util.Map;

public record AmbassadorRequest(
    String id,
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
