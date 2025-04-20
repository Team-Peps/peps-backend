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
    private static final String MESSAGE_PLACEHOLDER = "message";
    private static final String ERROR_PLACEHOLDER = "error";
    private static final String SLIDER_PLACEHOLDER = "slider";

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
                    MESSAGE_PLACEHOLDER, "Slider enregistré avec succès",
                    SLIDER_PLACEHOLDER, created
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la création du slider",
                    ERROR_PLACEHOLDER, e.getMessage()
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
                    MESSAGE_PLACEHOLDER, "Slider mis à jour avec succès",
                    SLIDER_PLACEHOLDER, updatedSlider
            ));
        } catch (Exception e) {
            log.error("❌ Error processing slider with ID: {}", slider.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors du traitement du slider",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteSlider(@PathVariable String id) {
        try {
            sliderService.deleteSlider(id);
            return ResponseEntity.ok(Map.of(MESSAGE_PLACEHOLDER, "Slider supprimé avec succès"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la suppression du slider",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/active")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleActive(@PathVariable String id) {
        try {
            SliderDto updatedSlider = sliderService.toggleActive(id);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Slider activé avec succès",
                    SLIDER_PLACEHOLDER, updatedSlider
            ));
        } catch (DataAccessException e) {
            log.error("❌ Error activating slider with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de l'activation du slider",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @PutMapping("/reorder")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateSliderOrder(@RequestBody List<String> orderedIds) {
        try {
            sliderService.updateSliderOrder(orderedIds);
            return ResponseEntity.ok(Map.of(MESSAGE_PLACEHOLDER, "Ordre des sliders mis à jour avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la mise à jour de l'ordre",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

}
