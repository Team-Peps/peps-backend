package fr.teampeps.mapper;

import fr.teampeps.dto.AchievementDto;
import fr.teampeps.models.Achievement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AchievementMapper {

    public AchievementDto toAchievementDto(Achievement achievement) {
        return AchievementDto.builder()
                .id(achievement.getId())
                .competitionName(achievement.getCompetitionName())
                .ranking(achievement.getRanking())
                .year(achievement.getYear())
                .game(achievement.getGame())
                .build();
    }

    public List<AchievementDto> toAchievementDtoList(List<Achievement> achievements) {
        return achievements.stream()
                .sorted(Comparator.comparing(Achievement::getYear).reversed()
                        .thenComparing(Achievement::getRanking))
                .map(this::toAchievementDto)
                .toList();
    }
}
