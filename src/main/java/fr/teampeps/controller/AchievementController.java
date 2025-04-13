package fr.teampeps.controller;

import fr.teampeps.dto.AchievementDto;
import fr.teampeps.model.Game;
import fr.teampeps.service.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/achievement")
@RequiredArgsConstructor
@Slf4j
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping("/game/{game}")
    public ResponseEntity<List<AchievementDto>> getAllAchievements(@PathVariable Game game) {
        return ResponseEntity.ok(achievementService.getAllAchievementsByGame(game));
    }

}
