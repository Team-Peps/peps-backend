package fr.teampeps.service;

import fr.teampeps.dto.VideoDto;
import fr.teampeps.mapper.VideoMapper;
import fr.teampeps.models.Video;
import fr.teampeps.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoMapper videoMapper;

    @InjectMocks
    private VideoService videoService;

    private Video video1;
    private Video video2;
    private VideoDto dto1;
    private VideoDto dto2;

    @BeforeEach
    void setUp() {
        video1 = new Video();
        video1.setId("1");
        video1.setTitle("Video 1");

        video2 = new Video();
        video2.setId("2");
        video2.setTitle("Video 2");

        dto1 = VideoDto.builder().build();
        dto1.setId("1");
        dto1.setTitle("Video 1");

        dto2 = VideoDto.builder().build();
        dto2.setId("2");
        dto2.setTitle("Video 2");
    }

    @Test
    void getLastVideos_returnsMappedDtos() {
        when(videoRepository.findAll()).thenReturn(List.of(video1, video2));
        when(videoMapper.toVideoDto(video1)).thenReturn(dto1);
        when(videoMapper.toVideoDto(video2)).thenReturn(dto2);

        List<VideoDto> result = videoService.getLastVideos();

        assertThat(result).containsExactly(dto1, dto2);
        verify(videoRepository).findAll();
        verify(videoMapper, times(2)).toVideoDto(any());
    }

    @Test
    void saveVideo_savesWhenUnderLimit() {
        when(videoRepository.count()).thenReturn(2L);
        when(videoRepository.save(video1)).thenReturn(video1);
        when(videoMapper.toVideoDto(video1)).thenReturn(dto1);

        VideoDto result = videoService.saveVideo(video1);

        assertThat(result).isEqualTo(dto1);
        verify(videoRepository).save(video1);
    }

    @Test
    void saveVideo_throwsConflictWhenAtLimit() {
        when(videoRepository.count()).thenReturn(3L);

        assertThatThrownBy(() -> videoService.saveVideo(video1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Impossible d'ajouter plus de 3 vidéos");

        verify(videoRepository, never()).save(any());
    }

    @Test
    void saveVideo_throwsInternalErrorOnException() {
        when(videoRepository.count()).thenReturn(1L);
        when(videoRepository.save(video1)).thenThrow(new RuntimeException("DB down"));

        assertThatThrownBy(() -> videoService.saveVideo(video1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Erreur lors de l'enregistrement de la vidéo");

        verify(videoRepository).save(video1);
    }

    @Test
    void updateVideo_updatesWhenExists() {
        when(videoRepository.existsById(video1.getId())).thenReturn(true);
        when(videoRepository.save(video1)).thenReturn(video1);
        when(videoMapper.toVideoDto(video1)).thenReturn(dto1);

        VideoDto result = videoService.updateVideo(video1);

        assertThat(result).isEqualTo(dto1);
        verify(videoRepository).save(video1);
    }

    @Test
    void updateVideo_throwsConflictWhenNotFound() {
        when(videoRepository.existsById(video1.getId())).thenReturn(false);

        assertThatThrownBy(() -> videoService.updateVideo(video1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Vidéo non trouvée");

        verify(videoRepository, never()).save(any());
    }

    @Test
    void updateVideo_throwsInternalErrorOnSaveException() {
        when(videoRepository.existsById(video1.getId())).thenReturn(true);
        when(videoRepository.save(video1)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> videoService.updateVideo(video1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Erreur lors de l'enregistrement de la vidéo");

        verify(videoRepository).save(video1);
    }
}
