package fr.teampeps.controller;


import fr.teampeps.dto.VideoDto;
import fr.teampeps.models.Video;
import fr.teampeps.service.VideoService;
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
@RequestMapping("/v1/last-videos")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final VideoService videoService;
    private static final String MESSAGE_PLACEHOLDER = "message";
    private static final String ERROR_PLACEHOLDER = "error";

    @GetMapping
    public ResponseEntity<List<VideoDto>> getLastVideos() {
        return ResponseEntity.ok(videoService.getLastVideos());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> createVideo(
            @RequestPart("video") Video video,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            VideoDto created = videoService.saveVideo(video, imageFile);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Succès : la vidéo a été enregistrée",
                    "video", created
            ));
        } catch (Exception e) {
            log.error("❌ Erreur lors de la création d'une vidéo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                MESSAGE_PLACEHOLDER, "Erreur lors de l'enregistrement de la vidéo",
                ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateVideo(
            @RequestPart("video") Video video,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            VideoDto updated = videoService.updateVideo(video, imageFile);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Vidéo mis à jour avec succès",
                    "video", updated
            ));
        } catch (Exception e) {
            log.error("❌ Error processing video with ID: {}", video.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors du traitement de la vidéo",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }
}
