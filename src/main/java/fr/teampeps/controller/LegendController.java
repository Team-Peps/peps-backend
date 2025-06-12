package fr.teampeps.controller;

import fr.teampeps.service.LegendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/legend")
@RequiredArgsConstructor
@Slf4j
public class LegendController {

    private final LegendService legendService;

    @GetMapping
    public ResponseEntity<List<String>> getAllHeroesByGame() {
        return ResponseEntity.ok(legendService.getAllLegends());
    }

    @PostMapping
    public ResponseEntity<String> addLegends(@RequestBody List<String> legends) {
        log.info("Received request to add legends: {}", legends);
        if (legends.isEmpty()) {
            return ResponseEntity.badRequest().body("La liste des légendes ne peut pas être vide !");
        }

        try {
            legendService.addLegends(legends);
            return ResponseEntity.status(HttpStatus.CREATED).body("Légendes ajoutées avec succès !");
        } catch (Exception e) {
            log.error("Erreur lors de l'ajout des légendes : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'ajout des légendes : " + e.getMessage());
        }
    }

}
