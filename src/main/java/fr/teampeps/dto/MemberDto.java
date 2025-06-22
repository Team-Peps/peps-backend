package fr.teampeps.dto;

import fr.teampeps.enums.Game;
import fr.teampeps.enums.MemberRole;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class MemberDto {
    private String id;
    private String pseudo;
    private String firstname;
    private String lastname;
    private String nationality;
    private Integer age;
    private String dateOfBirth;
    private MemberRole role;
    private Boolean isSubstitute;
    private Boolean isActive;
    private String imageKey;
    private String xUsername;
    private String instagramUsername;
    private String tiktokUsername;
    private String youtubeUsername;
    private String twitchUsername;
    private Game game;
    private List<AchievementDto> achievements;
    private List<HeroeDto> favoriteHeroes;
    private Map<String, MemberTranslationDto> translations;
}
