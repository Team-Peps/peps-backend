package fr.teampeps.controller;

import fr.teampeps.dto.AmbassadorDto;
import fr.teampeps.model.Ambassador;
import fr.teampeps.service.AmbassadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/ambassador")
@RequiredArgsConstructor
@Slf4j
public class AmbassadorController {

    private final AmbassadorService ambassadorService;

    @GetMapping
    public ResponseEntity<List<AmbassadorDto>> getAllAmbassadors() {
        return ResponseEntity.ok(ambassadorService.getAllAmbassadors());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateAmbassador(
            @RequestPart("ambassador") Ambassador ambassador,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        log.info(ambassador.getTwitchUsername());
        try {
            AmbassadorDto updatedAmbassador = ambassadorService.saveOrUpdateAmbassador(ambassador, imageFile);
            return ResponseEntity.ok(Map.of(
                    "message", "Ambassadeur mis à jour avec succès",
                    "ambassador", updatedAmbassador
            ));
        } catch (Exception e) {
            log.error("❌ Error processing ambassador with ID: {}", ambassador.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors du traitement de l'ambassadeur",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> saveAmbassador(
            @RequestPart("ambassador") Ambassador ambassador,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        try {
            AmbassadorDto updatedAmbassador = ambassadorService.saveOrUpdateAmbassador(ambassador, imageFile);
            return ResponseEntity.ok(Map.of(
                    "message", "Ambassadeur enregistré avec succès",
                    "ambassador", updatedAmbassador
            ));
        } catch (Exception e) {
            log.error("❌ Error processing ambassador with ID: {}", ambassador.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors du traitement de l'ambassadeur",
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteAmbassador(@PathVariable String id) {
        try {
            ambassadorService.deleteAmbassador(id);
                return ResponseEntity.ok(Map.of("message", "Ambassadeur supprimé avec succès"));
        } catch (DataAccessException e) {
            log.error("❌ Error deleting ambassador with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors de la suppression de l'ambassadeur",
                    "error", e.getMessage()
            ));
        }
    }
}
