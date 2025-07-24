package fr.teampeps.service;

import fr.teampeps.dto.AchievementDto;
import fr.teampeps.mapper.AchievementMapper;
import fr.teampeps.models.Achievement;
import fr.teampeps.enums.Game;
import fr.teampeps.repository.AchievementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final AchievementMapper achievementMapper;

    public List<AchievementDto> getAllAchievementsByGame(Game game) {
        return achievementRepository.findAllByGame(game).stream()
            .map(achievementMapper::toAchievementDto)
            .toList();
    }

    public AchievementDto saveGameAchievement(Achievement achievement) {
        try {

            achievement.setMember(null);
            Achievement saved = achievementRepository.save(achievement);
            return achievementMapper.toAchievementDto(saved);

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'enregistrement du palmarès avec ID: {}", achievement.getId(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la mise à jour du palmarès",
                    e
            );
        }
    }

    public void delete(String id) {
        try {
            achievementRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Palmarès non trouvé", e);
        }
    }

    public AchievementDto updateAchievement(String id, Achievement achievement) {
        Achievement existingAchievement = achievementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Palmarès non trouvé"));

        existingAchievement.setCompetitionName(achievement.getCompetitionName());
        existingAchievement.setGame(achievement.getGame());
        existingAchievement.setRanking(achievement.getRanking());
        existingAchievement.setYear(achievement.getYear());

        Achievement updated = achievementRepository.save(existingAchievement);
        return achievementMapper.toAchievementDto(updated);
    }
}
