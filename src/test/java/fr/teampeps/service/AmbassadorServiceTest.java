package fr.teampeps.service;

import fr.teampeps.dto.AmbassadorDto;
import fr.teampeps.dto.AmbassadorTranslationDto;
import fr.teampeps.mapper.AmbassadorMapper;
import fr.teampeps.models.Ambassador;
import fr.teampeps.enums.Bucket;
import fr.teampeps.record.AmbassadorRequest;
import fr.teampeps.record.AmbassadorTranslationRequest;
import fr.teampeps.repository.AmbassadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AmbassadorServiceTest {

    @Mock
    private AmbassadorRepository ambassadorRepository;

    @Mock
    private MinioService minioService;

    @Mock
    private AmbassadorMapper ambassadorMapper;

    @InjectMocks
    private AmbassadorService ambassadorService;

    private AmbassadorRequest ambassadorRequest;
    private Ambassador ambassador;
    private AmbassadorDto ambassadorDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ambassador = new Ambassador();
        ambassador.setId("ambassadorId");
        ambassador.setName("Fefe");

        ambassadorRequest = new AmbassadorRequest(
            "ambassadorId",
            "Fefe",
            "image_key.png",
            "twitter",
            "insta",
            "tiktok",
            "youtube",
            "twitch",
            Map.of(
                    "fr", new AmbassadorTranslationRequest("Description en français"),
                "en", new AmbassadorTranslationRequest("Description in English")
            )
        );

        ambassadorDto = AmbassadorDto.builder()
            .id("ambassadorId")
            .name("Fefe")
            .imageKey("image_key.png")
            .translations(Map.of(
                "fr", AmbassadorTranslationDto
                    .builder()
                    .description("Description en français")
                    .build(),
                "en", AmbassadorTranslationDto
                    .builder()
                    .description("Description in English")
                    .build()
            ))
            .build();


    }

    @Test
    void saveOrUpdateAmbassador_shouldSaveWithImage() {
        MultipartFile imageFile = mock(MultipartFile.class);
        String imageKey = "ambassadors/fefe.png";
        when(minioService.uploadImageFromMultipartFile(imageFile, "fefe", Bucket.AMBASSADORS)).thenReturn(imageKey);

        when(ambassadorRepository.save(ambassador)).thenReturn(ambassador);
        when(ambassadorMapper.toAmbassadorDto(ambassador)).thenReturn(ambassadorDto);
        when(ambassadorMapper.toAmbassador(ambassadorRequest)).thenReturn(ambassador);

        AmbassadorDto result = ambassadorService.saveAmbassador(ambassadorRequest, imageFile);

        assertThat(result).isEqualTo(ambassadorDto);
        verify(ambassadorRepository).save(ambassador);
        verify(minioService).uploadImageFromMultipartFile(imageFile, "fefe", Bucket.AMBASSADORS);
    }

    @Test
    void shouldThrowInternalErrorWhenMinioUploadFails() {
        MultipartFile imageFile = mock(MultipartFile.class);
        when(imageFile.isEmpty()).thenReturn(false);
        when(minioService.uploadImageFromMultipartFile(any(), any(), any())).thenThrow(new RuntimeException("MinIO error"));
        when(ambassadorMapper.toAmbassador(ambassadorRequest)).thenReturn(ambassador);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                ambassadorService.saveAmbassador(ambassadorRequest, imageFile)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Erreur lors de l'upload de l'image de l'ambassadeur"));
    }

    @Test
    void getAllAmbassadors_shouldReturnList() {
        Ambassador ambassador = new Ambassador();
        AmbassadorDto dto = AmbassadorDto.builder().build();

        when(ambassadorRepository.findAll()).thenReturn(List.of(ambassador));
        when(ambassadorMapper.toAmbassadorDto(ambassador)).thenReturn(dto);

        List<AmbassadorDto> result = ambassadorService.getAllAmbassadors();

        assertThat(result).containsExactly(dto);
    }

    @Test
    void deleteAmbassador_shouldDeleteById() {
        String id = "123";

        ambassadorService.deleteAmbassador(id);

        verify(ambassadorRepository).deleteById(id);
    }

    @Test
    void deleteAmbassador_shouldThrowNotFound() {
        String id = "123";

        doThrow(new EntityNotFoundException()).when(ambassadorRepository).deleteById(id);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                ambassadorService.deleteAmbassador(id)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Ambassadeur non trouvé"));
    }
}

