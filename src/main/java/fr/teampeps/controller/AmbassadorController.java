package fr.teampeps.controller;

import fr.teampeps.dto.AmbassadorDto;
import fr.teampeps.models.Ambassador;
import fr.teampeps.record.AmbassadorRequest;
import fr.teampeps.service.AmbassadorService;
import jakarta.validation.Valid;
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
    private static final String MESSAGE_PLACEHOLDER = "message";
    private static final String ERROR_PLACEHOLDER = "error";
    
    @GetMapping
    public ResponseEntity<List<AmbassadorDto>> getAllAmbassadors() {
        return ResponseEntity.ok(ambassadorService.getAllAmbassadors());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateAmbassador(
            @RequestPart("ambassador") AmbassadorRequest ambassadorRequest,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        AmbassadorDto updatedAmbassador = ambassadorService.updateAmbassador(ambassadorRequest, imageFile);
        return ResponseEntity.ok(Map.of(
                MESSAGE_PLACEHOLDER, "Ambassadeur mis à jour avec succès",
                "ambassador", updatedAmbassador
        ));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> saveAmbassador(
            @RequestPart("ambassador") @Valid AmbassadorRequest ambassadorRequest,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        AmbassadorDto updatedAmbassador = ambassadorService.saveAmbassador(ambassadorRequest, imageFile);
        return ResponseEntity.ok(Map.of(
                MESSAGE_PLACEHOLDER, "Ambassadeur enregistré avec succès",
                "ambassador", updatedAmbassador
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteAmbassador(@PathVariable String id) {
        try {
            ambassadorService.deleteAmbassador(id);
            return ResponseEntity.ok(Map.of(MESSAGE_PLACEHOLDER, "Ambassadeur supprimé avec succès"));
        } catch (DataAccessException e) {
            log.error("❌ Error deleting ambassador with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la suppression de l'ambassadeur",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }
}
