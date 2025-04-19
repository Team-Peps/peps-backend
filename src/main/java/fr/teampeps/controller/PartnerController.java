package fr.teampeps.controller;

import fr.teampeps.dto.PartnerDto;
import fr.teampeps.model.Partner;
import fr.teampeps.service.PartnerService;
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
@RequestMapping("/v1/partner")
@RequiredArgsConstructor
@Slf4j
public class PartnerController {

    private final PartnerService partnerService;
    private static final String MESSAGE_PLACEHOLDER = "message";
    private static final String ERROR_PLACEHOLDER = "error";
    private static final String PARTNER_PLACEHOLDER = "partner";
    
    @GetMapping
    public ResponseEntity<Map<String, List<PartnerDto>>> getAllPartners() {
        return ResponseEntity.ok(partnerService.getAllPartners());
    }

    @GetMapping("/active")
    public ResponseEntity<List<PartnerDto>> getAllActivePartners() {
        return ResponseEntity.ok(partnerService.getAllActivePartners());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePartner(
            @RequestPart("partner") Partner partner,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            PartnerDto updatedPartner = partnerService.saveOrUpdatePartner(partner, imageFile);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Partenaire mis à jour avec succès",
                    PARTNER_PLACEHOLDER, updatedPartner
            ));
        } catch (Exception e) {
            log.error("❌ Error processing partner with ID: {}", partner.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors du traitement du partenaire",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> savePartner(
            @RequestPart("partner") Partner partner,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        try {
            PartnerDto updatedPartner = partnerService.saveOrUpdatePartner(partner, imageFile);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Partenaire enregistré avec succès",
                    PARTNER_PLACEHOLDER, updatedPartner
            ));
        } catch (Exception e) {
            log.error("❌ Error processing partner with ID: {}", partner.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors du traitement du partenaire",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deletePartner(@PathVariable String id) {
        try {
            partnerService.deletePartner(id);
            return ResponseEntity.ok(Map.of(MESSAGE_PLACEHOLDER, "Partenaire supprimé avec succès"));
        } catch (DataAccessException e) {
            log.error("❌ Error deleting partner with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la suppression du partenaire",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/active")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleActive(@PathVariable String id) {
        try {
            PartnerDto updatedPartner = partnerService.toggleActive(id);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Partenaire activé avec succès",
                    PARTNER_PLACEHOLDER, updatedPartner
            ));
        } catch (DataAccessException e) {
            log.error("❌ Error activating partner with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de l'activation du partenaire",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }
}
