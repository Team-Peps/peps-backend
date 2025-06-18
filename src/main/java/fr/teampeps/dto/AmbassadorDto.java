package fr.teampeps.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AmbassadorDto {
    private String id;
    private String name;
    private String imageKey;
    private String twitterXUsername;
    private String instagramUsername;
    private String tiktokUsername;
    private String youtubeUsername;
    private String twitchUsername;
    private Map<String, AmbassadorTranslationDto> translations;

}
