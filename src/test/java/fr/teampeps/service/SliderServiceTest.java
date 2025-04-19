package fr.teampeps.service;

import fr.teampeps.dto.SliderDto;
import fr.teampeps.dto.SliderTinyDto;
import fr.teampeps.mapper.SliderMapper;
import fr.teampeps.model.Bucket;
import fr.teampeps.model.Slider;
import fr.teampeps.repository.SliderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SliderServiceTest {

    @Mock
    private SliderRepository sliderRepository;

    @Mock
    private SliderMapper sliderMapper;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private SliderService sliderService;

    @Captor
    private ArgumentCaptor<Slider> sliderCaptor;

    private Slider slider;
    private SliderDto sliderDto;
    private MultipartFile imageFile;
    private MultipartFile mobileImageFile;
    private List<Slider> activeSliders;
    private List<Slider> inactiveSliders;

    @BeforeEach
    void setUp() {
        // Setup test data
        slider = new Slider();
        slider.setId("slider123");
        slider.setIsActive(true);
        slider.setOrder(0L);

        sliderDto = SliderDto.builder().build();
        sliderDto.setId("slider123");

        SliderTinyDto sliderTinyDto = SliderTinyDto.builder().build();
        sliderTinyDto.setId("slider123");

        // Create mock multipart files
        imageFile = new MockMultipartFile(
                "image.jpg",
                "image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mobileImageFile = new MockMultipartFile(
                "mobile_image.jpg",
                "mobile_image.jpg",
                "image/jpeg",
                "test mobile image content".getBytes()
        );

        // Setup active and inactive sliders
        Slider activeSlider1 = new Slider();
        activeSlider1.setId("active1");
        activeSlider1.setIsActive(true);
        activeSlider1.setOrder(0L);

        Slider activeSlider2 = new Slider();
        activeSlider2.setId("active2");
        activeSlider2.setIsActive(true);
        activeSlider2.setOrder(1L);

        Slider inactiveSlider = new Slider();
        inactiveSlider.setId("inactive1");
        inactiveSlider.setIsActive(false);
        inactiveSlider.setOrder(-1L);

        activeSliders = Arrays.asList(activeSlider1, activeSlider2);
        inactiveSliders = Collections.singletonList(inactiveSlider);
    }

    @Test
    void getAllSliders_Success() {
        // Arrange
        SliderDto activeSliderDto1 = SliderDto.builder().build();
        activeSliderDto1.setId("active1");

        SliderDto activeSliderDto2 = SliderDto.builder().build();
        activeSliderDto2.setId("active2");

        SliderDto inactiveSliderDto = SliderDto.builder().build();
        inactiveSliderDto.setId("inactive1");

        when(sliderRepository.findAllByIsActiveOrderByOrder(true)).thenReturn(activeSliders);
        when(sliderRepository.findAllByIsActive(false)).thenReturn(inactiveSliders);

        when(sliderMapper.toSliderDto(activeSliders.get(0))).thenReturn(activeSliderDto1);
        when(sliderMapper.toSliderDto(activeSliders.get(1))).thenReturn(activeSliderDto2);
        when(sliderMapper.toSliderDto(inactiveSliders.get(0))).thenReturn(inactiveSliderDto);

        // Act
        Map<String, List<SliderDto>> result = sliderService.getAllSliders();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get("activeSliders").size());
        assertEquals(1, result.get("inactiveSliders").size());
        assertEquals("active1", result.get("activeSliders").get(0).getId());
        assertEquals("active2", result.get("activeSliders").get(1).getId());
        assertEquals("inactive1", result.get("inactiveSliders").get(0).getId());
    }

    @Test
    void getAllActiveSlider_Success() {
        // Arrange
        SliderTinyDto activeTinyDto1 = SliderTinyDto.builder().build();
        activeTinyDto1.setId("active1");

        SliderTinyDto activeTinyDto2 = SliderTinyDto.builder().build();
        activeTinyDto2.setId("active2");

        when(sliderRepository.findAllByIsActiveOrderByOrder(true)).thenReturn(activeSliders);
        when(sliderMapper.toSliderTinyDto(activeSliders.get(0))).thenReturn(activeTinyDto1);
        when(sliderMapper.toSliderTinyDto(activeSliders.get(1))).thenReturn(activeTinyDto2);

        // Act
        List<SliderTinyDto> result = sliderService.getAllActiveSlider();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("active1", result.get(0).getId());
        assertEquals("active2", result.get(1).getId());
    }

    @Test
    void testSaveSlider_Success() {
        // Arrange
        when(sliderRepository.count()).thenReturn(5L);
        when(minioService.uploadImageFromMultipartFile(eq(imageFile), any(String.class), eq(Bucket.SLIDERS)))
                .thenReturn("sliders/some-uuid");
        when(minioService.uploadImageFromMultipartFile(eq(mobileImageFile), any(String.class), eq(Bucket.SLIDERS)))
                .thenReturn("sliders/some-uuid_mobile");
        when(sliderRepository.save(any(Slider.class))).thenReturn(slider);
        when(sliderMapper.toSliderDto(slider)).thenReturn(sliderDto);

        // Act
        SliderDto result = sliderService.saveSlider(slider, imageFile, mobileImageFile);

        // Assert
        assertNotNull(result);
        assertEquals(sliderDto, result);
        assertEquals(5L, slider.getOrder());
        assertNotNull(slider.getImageKey());
        assertNotNull(slider.getMobileImageKey());

        verify(sliderRepository).count();
        verify(minioService).uploadImageFromMultipartFile(eq(imageFile), any(String.class), eq(Bucket.SLIDERS));
        verify(minioService).uploadImageFromMultipartFile(eq(mobileImageFile), any(String.class), eq(Bucket.SLIDERS));
        verify(sliderRepository).save(slider);
        verify(sliderMapper).toSliderDto(slider);
    }

    @Test
    void testSaveSlider_NullImageFile() {
        // Arrange
        imageFile = null;

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> sliderService.saveSlider(slider, imageFile, mobileImageFile));

        assertEquals("400 BAD_REQUEST \"Il faut fournir les deux images\"", exception.getMessage());
        verifyNoInteractions(minioService, sliderRepository, sliderMapper);
    }

    @Test
    void testSaveSlider_NullMobileImageFile() {
        // Arrange
        mobileImageFile = null;

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> sliderService.saveSlider(slider, imageFile, mobileImageFile));

        assertEquals("400 BAD_REQUEST \"Il faut fournir les deux images\"", exception.getMessage());
        verifyNoInteractions(minioService, sliderRepository, sliderMapper);
    }

    @Test
    void testSaveSlider_MinioServiceThrowsException() {
        // Arrange
        when(minioService.uploadImageFromMultipartFile(eq(imageFile), any(String.class), eq(Bucket.SLIDERS)))
                .thenThrow(new RuntimeException("Erreur Minio"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> sliderService.saveSlider(slider, imageFile, mobileImageFile));

        assertEquals("500 INTERNAL_SERVER_ERROR \"Erreur lors de la sauvegarde du slider\"", exception.getMessage());
        verify(minioService).uploadImageFromMultipartFile(eq(imageFile), any(String.class), eq(Bucket.SLIDERS));
        verifyNoMoreInteractions(sliderRepository, sliderMapper);
    }

    @Test
    void testSaveSlider_RepositoryThrowsException() {
        // Arrange
        when(minioService.uploadImageFromMultipartFile(eq(imageFile), any(String.class), eq(Bucket.SLIDERS)))
                .thenReturn("sliders/some-uuid");
        when(minioService.uploadImageFromMultipartFile(eq(mobileImageFile), any(String.class), eq(Bucket.SLIDERS)))
                .thenReturn("sliders/some-uuid_mobile");
        when(sliderRepository.count()).thenReturn(5L);
        when(sliderRepository.save(any(Slider.class))).thenThrow(new RuntimeException("Erreur DB"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> sliderService.saveSlider(slider, imageFile, mobileImageFile));

        assertEquals("500 INTERNAL_SERVER_ERROR \"Erreur lors de la sauvegarde du slider\"", exception.getMessage());
        verify(minioService).uploadImageFromMultipartFile(eq(imageFile), any(String.class), eq(Bucket.SLIDERS));
        verify(minioService).uploadImageFromMultipartFile(eq(mobileImageFile), any(String.class), eq(Bucket.SLIDERS));
        verify(sliderRepository).count();
        verify(sliderRepository).save(slider);
        verifyNoInteractions(sliderMapper);
    }

    @Test
    void testSaveSlider_VerifyUUIDGeneration() {
        // Ce test est plus avancé et vérifie que l'UUID est utilisé correctement
        // On utilise un ArgumentCaptor pour capturer l'argument passé à Minio

        // Arrange
        when(sliderRepository.count()).thenReturn(0L);
        when(sliderRepository.save(any(Slider.class))).thenReturn(slider);
        when(sliderMapper.toSliderDto(slider)).thenReturn(sliderDto);

        // Act
        sliderService.saveSlider(slider, imageFile, mobileImageFile);

        // Assert
        // On vérifie que les appels à Minio sont faits avec le même UUID de base
        verify(minioService).uploadImageFromMultipartFile(eq(imageFile), any(String.class), eq(Bucket.SLIDERS));
        verify(minioService).uploadImageFromMultipartFile(eq(mobileImageFile), argThat(arg ->
                arg != null && arg.endsWith("_mobile")), eq(Bucket.SLIDERS));
    }

    @Test
    void updateSlider_Success() {
        // Arrange
        when(minioService.uploadImageFromMultipartFile(eq(imageFile), eq("slider123"), eq(Bucket.SLIDERS)))
                .thenReturn("sliders/slider123.jpg");
        when(minioService.uploadImageFromMultipartFile(eq(mobileImageFile), eq("slider123_mobile"), eq(Bucket.SLIDERS)))
                .thenReturn("sliders/slider123_mobile.jpg");
        when(sliderRepository.findById("slider123")).thenReturn(Optional.of(slider));
        when(sliderRepository.save(any(Slider.class))).thenReturn(slider);
        when(sliderMapper.toSliderDto(slider)).thenReturn(sliderDto);

        // Act
        SliderDto result = sliderService.updateSlider(slider, imageFile, mobileImageFile);

        // Assert
        verify(sliderRepository).save(sliderCaptor.capture());
        Slider capturedSlider = sliderCaptor.getValue();

        assertNotNull(result);
        assertEquals(sliderDto, result);
        assertEquals("sliders/slider123.jpg", capturedSlider.getImageKey());
        assertEquals("sliders/slider123_mobile.jpg", capturedSlider.getMobileImageKey());
        assertEquals(0L, capturedSlider.getOrder());
    }

    @Test
    void updateSlider_NullImageFile() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                sliderService.updateSlider(slider, null, mobileImageFile));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Il faut fournir les deux images", exception.getReason());
    }

    @Test
    void updateSlider_SliderNotFound() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                sliderService.updateSlider(slider, imageFile, mobileImageFile));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Slider non trouvé", exception.getReason());
    }

    @Test
    void updateSlider_MinioServiceThrowsException() {
        // Arrange
        when(sliderRepository.findById("slider123")).thenReturn(Optional.of(slider));
        when(minioService.uploadImageFromMultipartFile(eq(imageFile), eq("slider123"), eq(Bucket.SLIDERS)))
                .thenThrow(new RuntimeException("Erreur Minio"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                sliderService.updateSlider(slider, imageFile, mobileImageFile));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Erreur lors de la mise à jour du slider", exception.getReason());
    }

    @Test
    void deleteSlider_Success() {
        // Act
        sliderService.deleteSlider("slider123");

        // Assert
        verify(sliderRepository).deleteById("slider123");
    }

    @Test
    void deleteSlider_NotFound() {
        // Arrange
        doThrow(new EntityNotFoundException("Slider not found")).when(sliderRepository).deleteById("nonexistent");

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                sliderService.deleteSlider("nonexistent"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Slider non trouvé", exception.getReason());
    }

    @Test
    void toggleActive_ActiveToInactive() {
        // Arrange
        when(sliderRepository.findById("slider123")).thenReturn(Optional.of(slider));
        when(sliderRepository.save(any(Slider.class))).thenReturn(slider);
        when(sliderMapper.toSliderDto(slider)).thenReturn(sliderDto);

        // Act
        SliderDto result = sliderService.toggleActive("slider123");

        // Assert
        verify(sliderRepository).save(sliderCaptor.capture());
        Slider capturedSlider = sliderCaptor.getValue();

        assertNotNull(result);
        assertEquals(sliderDto, result);
        assertFalse(capturedSlider.getIsActive());
        assertEquals(-1L, capturedSlider.getOrder());
    }

    @Test
    void toggleActive_InactiveToActive() {
        // Arrange
        Slider inactiveSlider = new Slider();
        inactiveSlider.setId("slider123");
        inactiveSlider.setIsActive(false);
        inactiveSlider.setOrder(-1L);

        when(sliderRepository.findById("slider123")).thenReturn(Optional.of(inactiveSlider));
        when(sliderRepository.save(any(Slider.class))).thenReturn(inactiveSlider);
        when(sliderMapper.toSliderDto(inactiveSlider)).thenReturn(sliderDto);

        // Act
        SliderDto result = sliderService.toggleActive("slider123");

        // Assert
        verify(sliderRepository).save(sliderCaptor.capture());
        Slider capturedSlider = sliderCaptor.getValue();

        assertNotNull(result);
        assertEquals(sliderDto, result);
        assertTrue(capturedSlider.getIsActive());
        // Order should remain -1L since we're just toggling active status
        assertEquals(-1L, capturedSlider.getOrder());
    }

    @Test
    void toggleActive_SliderNotFound() {
        // Arrange
        when(sliderRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                sliderService.toggleActive("nonexistent"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Slider non trouvé", exception.getReason());
    }

    @Test
    void updateSliderOrder_Success() {
        // Arrange
        Slider slider1 = new Slider();
        slider1.setId("slider1");

        Slider slider2 = new Slider();
        slider2.setId("slider2");

        Slider slider3 = new Slider();
        slider3.setId("slider3");

        when(sliderRepository.findById("slider1")).thenReturn(Optional.of(slider1));
        when(sliderRepository.findById("slider2")).thenReturn(Optional.of(slider2));
        when(sliderRepository.findById("slider3")).thenReturn(Optional.of(slider3));

        List<String> orderedIds = Arrays.asList("slider1", "slider2", "slider3");

        // Act
        sliderService.updateSliderOrder(orderedIds);

        // Assert
        verify(sliderRepository).save(argThat(arg -> arg.getId().equals("slider1") && arg.getOrder() == 0L));
        verify(sliderRepository).save(argThat(arg -> arg.getId().equals("slider2") && arg.getOrder() == 1L));
        verify(sliderRepository).save(argThat(arg -> arg.getId().equals("slider3") && arg.getOrder() == 2L));
    }

    @Test
    void updateSliderOrder_WithMissingSlider() {
        // Arrange
        Slider slider1 = new Slider();
        slider1.setId("slider1");

        Slider slider3 = new Slider();
        slider3.setId("slider3");

        when(sliderRepository.findById("slider1")).thenReturn(Optional.of(slider1));
        when(sliderRepository.findById("slider2")).thenReturn(Optional.empty());
        when(sliderRepository.findById("slider3")).thenReturn(Optional.of(slider3));

        List<String> orderedIds = Arrays.asList("slider1", "slider2", "slider3");

        // Act
        sliderService.updateSliderOrder(orderedIds);

        // Assert
        verify(sliderRepository).save(argThat(arg -> arg.getId().equals("slider1") && arg.getOrder() == 0L));
        verify(sliderRepository, never()).save(argThat(arg -> arg.getId().equals("slider2")));
        verify(sliderRepository).save(argThat(arg -> arg.getId().equals("slider3") && arg.getOrder() == 2L));
    }
}