package fr.teampeps.service;

import fr.teampeps.dto.AchievementDto;
import fr.teampeps.mapper.AchievementMapper;
import fr.teampeps.model.Game;
import fr.teampeps.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final AchievementMapper achievementMapper;

    public List<AchievementDto> getAllAchievementsByGame(Game game) {
        return achievementRepository.findAllByGame(game).stream()
            .map(achievementMapper::toAchievementDto)
            .toList();
    }

}
