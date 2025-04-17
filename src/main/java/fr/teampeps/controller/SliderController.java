package fr.teampeps.controller;

import fr.teampeps.dto.SliderDto;
import fr.teampeps.dto.SliderTinyDto;
import fr.teampeps.model.Slider;
import fr.teampeps.service.SliderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/slider")
@RequiredArgsConstructor
@Slf4j
public class SliderController {

    private final SliderService sliderService;

    @GetMapping
    public ResponseEntity<Map<String, List<SliderDto>>> getAllSliders() {
        return ResponseEntity.ok(sliderService.getAllSliders());
    }

    @GetMapping("/active")
    public ResponseEntity<List<SliderTinyDto>> getAllActiveSlider() {
        return ResponseEntity.ok(sliderService.getAllActiveSlider());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> createSlider(
            @RequestPart("slider") Slider slider,
            @RequestPart("imageFile") MultipartFile imageFile,
            @RequestPart("mobileImageFile") MultipartFile mobileImageFile
    ) {
        try {
            SliderDto created = sliderService.saveSlider(slider, imageFile, mobileImageFile);
            return ResponseEntity.ok(Map.of(
                    "message", "Slider enregistré avec succès",
                    "slider", created
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors de la création du slider",
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateSlider(
            @RequestPart("slider") Slider slider,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "mobileImageFile", required = false) MultipartFile mobileImageFile
    ) {
        try {
            SliderDto updatedSlider = sliderService.updateSlider(slider, imageFile, mobileImageFile);
            return ResponseEntity.ok(Map.of(
                    "message", "Slider mis à jour avec succès",
                    "slider", updatedSlider
            ));
        } catch (Exception e) {
            log.error("❌ Error processing slider with ID: {}", slider.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors du traitement du slider",
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteSlider(@PathVariable String id) {
        try {
            sliderService.deleteSlider(id);
            return ResponseEntity.ok(Map.of("message", "Slider supprimé avec succès"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "message", "Erreur lors de la suppression du slider",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/active")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleActive(@PathVariable String id) {
        try {
            SliderDto updatedSlider = sliderService.toggleActive(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Slider activé avec succès",
                    "slider", updatedSlider
            ));
        } catch (DataAccessException e) {
            log.error("❌ Error activating slider with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors de l'activation du slider",
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/reorder")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateSliderOrder(@RequestBody List<String> orderedIds) {
        try {
            sliderService.updateSliderOrder(orderedIds);
            return ResponseEntity.ok(Map.of("message", "Ordre des sliders mis à jour avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors de la mise à jour de l'ordre",
                    "error", e.getMessage()
            ));
        }
    }

}
