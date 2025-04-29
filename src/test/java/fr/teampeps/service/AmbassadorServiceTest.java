package fr.teampeps.service;

import fr.teampeps.dto.AmbassadorDto;
import fr.teampeps.mapper.AmbassadorMapper;
import fr.teampeps.models.Ambassador;
import fr.teampeps.enums.Bucket;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveOrUpdateAmbassador_shouldSaveWithImage() {
        Ambassador ambassador = new Ambassador();
        ambassador.setName("Peps");

        MultipartFile imageFile = mock(MultipartFile.class);
        String imageKey = "ambassadors/peps.png";
        when(minioService.uploadImageFromMultipartFile(imageFile, "peps", Bucket.AMBASSADORS)).thenReturn(imageKey);

        Ambassador savedAmbassador = new Ambassador();
        savedAmbassador.setImageKey(imageKey);
        AmbassadorDto dto = AmbassadorDto.builder().build();

        when(ambassadorRepository.save(ambassador)).thenReturn(savedAmbassador);
        when(ambassadorMapper.toAmbassadorDto(savedAmbassador)).thenReturn(dto);

        AmbassadorDto result = ambassadorService.saveAmbassador(ambassador, imageFile);

        assertThat(result).isEqualTo(dto);
        verify(ambassadorRepository).save(ambassador);
        verify(minioService).uploadImageFromMultipartFile(imageFile, "peps", Bucket.AMBASSADORS);
    }

    @Test
    void shouldThrowBadRequestWhenImageFileIsNull() {
        Ambassador ambassador = new Ambassador();
        ambassador.setName("Test");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                ambassadorService.saveAmbassador(ambassador, null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Aucune image fournie"));
    }

    @Test
    void shouldThrowBadRequestWhenImageFileIsEmpty() {
        Ambassador ambassador = new Ambassador();
        ambassador.setName("Test");

        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                ambassadorService.saveAmbassador(ambassador, emptyFile)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Aucune image fournie"));
    }

    @Test
    void shouldThrowInternalErrorWhenMinioUploadFails() {
        Ambassador ambassador = new Ambassador();
        ambassador.setName("Test");

        MultipartFile imageFile = mock(MultipartFile.class);
        when(imageFile.isEmpty()).thenReturn(false);
        when(minioService.uploadImageFromMultipartFile(any(), any(), any())).thenThrow(new RuntimeException("MinIO error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                ambassadorService.saveAmbassador(ambassador, imageFile)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Erreur lors de la mise à jour"));
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

