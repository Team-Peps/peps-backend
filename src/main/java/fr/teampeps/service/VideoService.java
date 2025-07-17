package fr.teampeps.service;

import fr.teampeps.dto.VideoDto;
import fr.teampeps.enums.Bucket;
import fr.teampeps.mapper.VideoMapper;
import fr.teampeps.models.Video;
import fr.teampeps.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;
    private final MinioService minioService;

    public List<VideoDto> getLastVideos() {
        return videoRepository.findAll().stream()
                .map(videoMapper::toVideoDto)
                .toList();
    }

    public VideoDto saveVideo(Video video, MultipartFile imageFile) {
        if(videoRepository.count() >=3) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Impossible d'ajouter plus de 3 vidéos");
        }

        if(imageFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image non fournie");
        }

        try {
            String idFromLink = UUID.randomUUID().toString();
            String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, idFromLink, Bucket.VIDEOS);
            video.setImageKey(imageUrl);

            return videoMapper.toVideoDto(videoRepository.save(video));
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'enregistrement de la vidéo avec ID: {}", video.getId(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de l'enregistrement de la vidéo",
                    e
            );
        }
    }

    public VideoDto updateVideo(Video video, MultipartFile imageFile) {

        if(!videoRepository.existsById(video.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vidéo non trouvée");
        }

        try {

            if(imageFile != null) {
                String idFromLink = UUID.fromString(video.getLink()).toString();
                String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, idFromLink, Bucket.VIDEOS);
                video.setImageKey(imageUrl);
            }

            return videoMapper.toVideoDto(videoRepository.save(video));
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'enregistrement de la vidéo avec ID: {}", video.getId(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de l'enregistrement de la vidéo",
                    e
            );
        }
    }
}
