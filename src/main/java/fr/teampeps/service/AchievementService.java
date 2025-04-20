package fr.teampeps.service;

import fr.teampeps.dto.AchievementDto;
import fr.teampeps.mapper.AchievementMapper;
import fr.teampeps.models.Achievement;
import fr.teampeps.enums.Game;
import fr.teampeps.models.Member;
import fr.teampeps.repository.AchievementRepository;
import fr.teampeps.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final AchievementMapper achievementMapper;
    private final MemberRepository memberRepository;

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

    public AchievementDto saveMemberAchievement(Achievement achievement, String memberId) {
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Membre non trouvé");
        }

        try {

            achievement.setMember(memberOptional.get());
            achievement.setGame(memberOptional.get().getGame());
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

    public List<AchievementDto> getAllAchievementsByMember(String memberId) {
        return achievementRepository.findAllByMemberId(memberId).stream()
                .map(achievementMapper::toAchievementDto)
                .toList();
    }
}
