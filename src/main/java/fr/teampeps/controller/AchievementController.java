package fr.teampeps.controller;

import fr.teampeps.dto.AchievementDto;
import fr.teampeps.models.Achievement;
import fr.teampeps.enums.Game;
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
    private static final String MESSAGE_PLACEHOLDER = "message";
    private static final String ERROR_PLACEHOLDER = "error";

    @GetMapping("/game/{game}")
    public ResponseEntity<List<AchievementDto>> getAllAchievementsByGame(@PathVariable Game game) {
        return ResponseEntity.ok(achievementService.getAllAchievementsByGame(game));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> createGameAchievement(
            @RequestBody Achievement achievement
    ) {
        try {
            AchievementDto created = achievementService.saveGameAchievement(achievement);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Succès : le palmarès a été enregistré",
                    "achievement", created
            ));
        } catch (Exception e) {
            log.error("❌ Erreur lors de la création d'un palmarès", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de l'enregistrement du palmarès",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateAchievement(
            @PathVariable String id,
            @RequestBody Achievement achievement
    ) {
        try {
            AchievementDto updated = achievementService.updateAchievement(id, achievement);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Succès : palmarès mis à jour",
                    "achievement", updated
            ));
        } catch (DataAccessException e) {
            log.error("❌ Erreur lors de la mise à jour du palmarès avec ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la mise à jour du palmarès",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteAchievement(@PathVariable String id) {
        try {
            achievementService.delete(id);
            return ResponseEntity.ok(Map.of(MESSAGE_PLACEHOLDER, "Succès : palmarès supprimé"));
        } catch (DataAccessException e) {
            log.error("❌ Erreur lors de la suppression du palmarès avec ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la suppression du palmarès",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

}
