package fr.teampeps.service;

import fr.teampeps.dto.VideoDto;
import fr.teampeps.mapper.VideoMapper;
import fr.teampeps.models.Video;
import fr.teampeps.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;

    public List<VideoDto> getLastVideos() {
        return videoRepository.findAll().stream()
                .map(videoMapper::toVideoDto)
                .toList();
    }

    public VideoDto saveVideo(Video video) {
        if(videoRepository.count() >=3) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Impossible d'ajouter plus de 3 vidéos");
        }

        try {
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

    public VideoDto updateVideo(Video video) {

        if(!videoRepository.existsById(video.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vidéo non trouvée");
        }

        try {
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
