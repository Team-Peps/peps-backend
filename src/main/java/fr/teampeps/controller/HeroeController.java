package fr.teampeps.controller;

import fr.teampeps.dto.HeroeDto;
import fr.teampeps.model.heroe.Heroe;
import fr.teampeps.service.HeroeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/heroe")
@RequiredArgsConstructor
@Slf4j
public class HeroeController {

    private final HeroeService heroeService;

    @GetMapping
    public ResponseEntity<Map<String, List<HeroeDto>>> getAllHeroesByGame() {
        return ResponseEntity.ok(heroeService.getAllHeroes());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateHeroe(
            @RequestPart("heroe") Heroe heroe,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        log.info("Updating partner : {}", heroe);
        try {
            HeroeDto updatedHeroe = heroeService.saveOrUpdateHeroe(heroe, imageFile);
            return ResponseEntity.ok(Map.of(
                    "message", "Héro mis à jour avec succès",
                    "heroe", updatedHeroe
            ));
        } catch (Exception e) {
            log.error("❌ Error processing heroe with ID: {}", heroe.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors du traitement du héro",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> saveHeroe(
            @RequestPart("heroe") Heroe heroe,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        try {
            HeroeDto updatedHeroe = heroeService.saveOrUpdateHeroe(heroe, imageFile);
            return ResponseEntity.ok(Map.of(
                    "message", "Héro enregistré avec succès",
                    "heroe", updatedHeroe
            ));
        } catch (Exception e) {
            log.error("❌ Error processing heroe with ID: {}", heroe.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors du traitement du partenaire",
                    "error", e.getMessage()
            ));
        }
    }
}
