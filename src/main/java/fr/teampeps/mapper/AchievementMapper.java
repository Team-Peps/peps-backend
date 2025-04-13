package fr.teampeps.mapper;

import fr.teampeps.dto.AchievementDto;
import fr.teampeps.model.Achievement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AchievementMapper {

    public AchievementDto toAchievementDto(Achievement achievement) {
        return AchievementDto.builder()
                .id(achievement.getId())
                .competitionName(achievement.getCompetitionName())
                .ranking(achievement.getRanking())
                .game(achievement.getGame())
                .build();
    }

    public List<AchievementDto> toAchievementDtoList(List<Achievement> achievements) {
        return achievements.stream()
                .map(this::toAchievementDto)
                .toList();
    }
}
