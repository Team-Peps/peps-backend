package fr.teampeps.controller;

import fr.teampeps.dto.AchievementDto;
import fr.teampeps.model.Achievement;
import fr.teampeps.model.Game;
import fr.teampeps.service.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/achievement")
@RequiredArgsConstructor
@Slf4j
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping("/game/{game}")
    public ResponseEntity<List<AchievementDto>> getAllAchievementsByGame(@PathVariable Game game) {
        return ResponseEntity.ok(achievementService.getAllAchievementsByGame(game));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<AchievementDto>> getAllAchievementsByMember(@PathVariable String memberId) {
        return ResponseEntity.ok(achievementService.getAllAchievementsByMember(memberId));
    }

    @PostMapping("/game")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> createGameAchievement(
            @RequestBody Achievement achievement
    ) {
        try {
            AchievementDto created = achievementService.saveGameAchievement(achievement);
            return ResponseEntity.ok(Map.of(
                    "message", "Succès : le palmarès a été enregistré",
                    "achievement", created
            ));
        } catch (Exception e) {
            log.error("❌ Erreur lors de la création d'un palmarès", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors de l'enregistrement du palmarès",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/member/{memberId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> createMemberAchievement(
            @RequestBody Achievement achievement,
            @PathVariable String memberId
    ) {
        try {
            AchievementDto created = achievementService.saveMemberAchievement(achievement, memberId);
            return ResponseEntity.ok(Map.of(
                    "message", "Succès : le palmarès a été enregistré",
                    "achievement", created
            ));
        } catch (Exception e) {
            log.error("❌ Erreur lors de la création d'un palmarès", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors de l'enregistrement du palmarès",
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteAchievement(@PathVariable String id) {
        try {
            achievementService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Succès : palmarès supprimé"));
        } catch (DataAccessException e) {
            log.error("❌ Erreur lors de la suppression du palmarès avec ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors de la suppression du palmarès",
                    "error", e.getMessage()
            ));
        }
    }

}
