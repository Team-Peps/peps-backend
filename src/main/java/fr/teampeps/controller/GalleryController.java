package fr.teampeps.controller;

import fr.teampeps.dto.*;
import fr.teampeps.models.Author;
import fr.teampeps.record.GalleryRequest;
import fr.teampeps.service.GalleryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/gallery")
@RequiredArgsConstructor
@Slf4j
public class GalleryController {

    private final GalleryService galleryService;
    private static final String MESSAGE_PLACEHOLDER = "message";
    private static final String ERROR_PLACEHOLDER = "error";

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> createGallery(
            @RequestPart("gallery") GalleryRequest galleryRequest,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        log.info("📦 Creating gallery with date : {}", galleryRequest.date());
        try {
            GalleryDto createdGallery = galleryService.createGallery(galleryRequest, imageFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Galerie créée avec succès",
                    "gallery", createdGallery
            ));
        } catch (Exception e) {
            log.error("❌ Error creating gallery : {}", galleryRequest.date(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la création de la galerie",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @PutMapping("/{galleryId}/photos")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> addPhotosToGallery(
            @PathVariable("galleryId") String galleryId,
            @RequestPart(value = "zipFile") MultipartFile zipFile,
            @RequestParam(value = "author") Author author
    ) {
        log.info("📦 Processing gallery with ID: {}", galleryId);
        try {
            GalleryDto gallery = galleryService.addPhotosToGallery(galleryId, zipFile, author);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Photos ajoutés à la galerie avec succès",
                    "gallery", gallery
            ));
        } catch (Exception e) {
            log.error("❌ Error processing gallery with ID: {}", galleryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors du traitement des photos",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @PutMapping("/{galleryId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateGallery(
            @PathVariable("galleryId") String galleryId,
            @RequestPart("gallery") GalleryRequest galleryRequest,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile

    ) {
        log.info("📝 Updating gallery with ID: {}", galleryId);
        try {
            GalleryDto updatedGallery = galleryService.updateGallery(galleryId, galleryRequest, imageFile);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Galerie mise à jour avec succès",
                    "gallery", updatedGallery
            ));
        } catch (Exception e) {
            log.error("❌ Error updating gallery with ID: {}", galleryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la mise à jour de la galerie",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<GalleryDto>> getAllGallery() {
        return ResponseEntity.ok(galleryService.getAllGallery());
    }

    @DeleteMapping("/{galleryId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteGallery(@PathVariable("galleryId") String galleryId) {
        log.info("🗑️ Deleting gallery with ID: {}", galleryId);
        try {
            galleryService.deleteGallery(galleryId);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Galerie supprimée avec succès"
            ));
        } catch (Exception e) {
            log.error("❌ Error deleting gallery with ID: {}", galleryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la suppression de la galerie",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @DeleteMapping("/photo/{photoId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deletePhotoFromGallery(
            @PathVariable("photoId") String photoId
    ) {
        log.info("🗑️ Deleting photo with ID: {}", photoId);
        try {
            galleryService.deletePhoto(photoId);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Photo supprimée de la galerie avec succès"
            ));
        } catch (Exception e) {
            log.error("❌ Error deleting photo with ID: {}", photoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la suppression de la photo",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<Page<GalleryTinyDto>> getGalleries(
            @RequestParam(defaultValue = "0") int page
    ) {
        Page<GalleryTinyDto> galleries = galleryService.getGalleries(page);
        return ResponseEntity.ok(galleries);
    }

    @GetMapping("/{galleryId}")
    public ResponseEntity<GalleryDto> getGalleryById(@PathVariable("galleryId") String galleryId) {
        GalleryDto gallery = galleryService.getGalleryById(galleryId);
        return ResponseEntity.ok(gallery);
    }

}
