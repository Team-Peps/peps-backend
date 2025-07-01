package fr.teampeps.service;

import fr.teampeps.dto.SliderDto;
import fr.teampeps.dto.SliderTranslationDto;
import fr.teampeps.mapper.SliderMapper;
import fr.teampeps.enums.Bucket;
import fr.teampeps.models.Slider;
import fr.teampeps.record.SliderRequest;
import fr.teampeps.record.SliderTranslationRequest;
import fr.teampeps.repository.SliderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
    private SliderRequest sliderRequest;
    private SliderDto sliderDto;

    private MultipartFile imageFileFr;
    private MultipartFile mobileImageFileFr;
    private MultipartFile imageFileEn;
    private MultipartFile mobileImageFileEn;

    private List<Slider> activeSliders;
    private List<Slider> inactiveSliders;

    private SliderDto activeDto1;
    private SliderDto activeDto2;

    @BeforeEach
    void setUp() {

        sliderRequest = new SliderRequest(
                "sliderId",
                "https://example.com/cta",
                true,
                Map.of(
                        "fr", new SliderTranslationRequest("Fr CTA", "fr_image.jpg", "fr_mobile_image.jpg"),
                        "en", new SliderTranslationRequest("En CTA", "en_image.jpg", "en_mobile_image.jpg")
                )
        );

        // Setup test data
        slider = new Slider();
        slider.setId("sliderId");
        slider.setIsActive(true);
        slider.setOrder(0L);

        sliderDto = SliderDto.builder().build();
        sliderDto.setId("sliderId");
        sliderDto.setTranslations(Map.of(
                "fr", SliderTranslationDto.builder()
                        .ctaLabel("Fr CTA")
                        .imageKey("fr_image.jpg")
                        .mobileImageKey("fr_mobile_image.jpg")
                        .build()
                ));

        // Create mock multipart files
        imageFileFr = new MockMultipartFile(
                "fr_image.jpg",
                "fr_image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mobileImageFileFr = new MockMultipartFile(
                "fr_mobile_image.jpg",
                "fr_mobile_image.jpg",
                "image/jpeg",
                "test mobile image content".getBytes()
        );

        imageFileEn = new MockMultipartFile(
                "en_image.jpg",
                "en_image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mobileImageFileEn = new MockMultipartFile(
                "en_mobile_image.jpg",
                "en_mobile_image.jpg",
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

        activeDto1 = SliderDto.builder().id("active1").build();
        activeDto2 = SliderDto.builder().id("active2").build();

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

        when(sliderRepository.findAllByIsActiveOrderByOrder(true)).thenReturn(activeSliders);
        when(sliderMapper.toSliderDto(activeSliders.get(0))).thenReturn(activeDto1);
        when(sliderMapper.toSliderDto(activeSliders.get(1))).thenReturn(activeDto2);

        // Act
        List<SliderDto> result = sliderService.getAllActiveSlider();

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
        when(minioService.uploadImageFromMultipartFile(eq(imageFileFr), any(String.class), eq(Bucket.SLIDERS)))
                .thenReturn("sliders/some-uuid-fr");
        when(minioService.uploadImageFromMultipartFile(eq(mobileImageFileFr), any(String.class), eq(Bucket.SLIDERS)))
                .thenReturn("sliders/some-uuid_mobile-fr");
        when(minioService.uploadImageFromMultipartFile(eq(imageFileEn), any(String.class), eq(Bucket.SLIDERS)))
                .thenReturn("sliders/some-uuid-en");
        when(minioService.uploadImageFromMultipartFile(eq(mobileImageFileEn), any(String.class), eq(Bucket.SLIDERS)))
                .thenReturn("sliders/some-uuid_mobile-en");
        when(sliderRepository.save(any(Slider.class))).thenReturn(slider);
        when(sliderMapper.toSliderDto(slider)).thenReturn(sliderDto);
        when(sliderMapper.toSlider(sliderRequest)).thenReturn(slider);

        // Act
        SliderDto result = sliderService.saveSlider(sliderRequest, imageFileFr, mobileImageFileFr, imageFileEn, mobileImageFileEn);

        // Assert
        assertNotNull(result);
        assertEquals(sliderDto, result);
        assertEquals(5L, slider.getOrder());
        log.info(result.toString());
        assertNotNull(result.getTranslations().get("fr"));

        verify(sliderRepository).count();
        verify(minioService).uploadImageFromMultipartFile(eq(imageFileFr), any(String.class), eq(Bucket.SLIDERS));
        verify(minioService).uploadImageFromMultipartFile(eq(imageFileEn), any(String.class), eq(Bucket.SLIDERS));
        verify(minioService).uploadImageFromMultipartFile(eq(mobileImageFileFr), any(String.class), eq(Bucket.SLIDERS));
        verify(minioService).uploadImageFromMultipartFile(eq(mobileImageFileEn), any(String.class), eq(Bucket.SLIDERS));
        verify(sliderRepository).save(slider);
        verify(sliderMapper).toSliderDto(slider);
    }

    @Test
    void testSaveSlider_NullImageFile() {
        // Arrange
        imageFileFr = null;

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> sliderService.saveSlider(sliderRequest, imageFileFr, mobileImageFileFr, imageFileEn, mobileImageFileEn));

        assertEquals("400 BAD_REQUEST \"Il faut fournir les quatre images\"", exception.getMessage());
        verifyNoInteractions(minioService, sliderRepository, sliderMapper);
    }

    @Test
    void testSaveSlider_NullMobileImageFile() {
        // Arrange
        mobileImageFileFr = null;

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> sliderService.saveSlider(sliderRequest, imageFileFr, mobileImageFileFr, imageFileEn, mobileImageFileEn));

        assertEquals("400 BAD_REQUEST \"Il faut fournir les quatre images\"", exception.getMessage());
        verifyNoInteractions(minioService, sliderRepository, sliderMapper);
    }

    @Test
    void updateSlider_SliderNotFound() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                sliderService.updateSlider(sliderRequest, imageFileFr, mobileImageFileFr, imageFileEn, mobileImageFileEn));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Slider non trouvé", exception.getReason());
    }

    @Test
    void updateSlider_MinioServiceThrowsException() {
        // Arrange
        when(sliderRepository.findById("sliderId")).thenReturn(Optional.of(slider));
        when(minioService.uploadImageFromMultipartFile(eq(imageFileFr), eq("sliderId"), eq(Bucket.SLIDERS)))
                .thenThrow(new RuntimeException("Erreur Minio"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                sliderService.updateSlider(sliderRequest, imageFileFr, mobileImageFileFr, imageFileEn, mobileImageFileEn));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Erreur lors de la mise à jour du slider", exception.getReason());
    }

    @Test
    void deleteSlider_Success() {
        // Act
        sliderService.deleteSlider("sliderId");

        // Assert
        verify(sliderRepository).deleteById("sliderId");
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
        when(sliderRepository.findById("sliderId")).thenReturn(Optional.of(slider));
        when(sliderRepository.save(any(Slider.class))).thenReturn(slider);
        when(sliderMapper.toSliderDto(slider)).thenReturn(sliderDto);

        // Act
        SliderDto result = sliderService.toggleActive("sliderId");

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
        inactiveSlider.setId("sliderId");
        inactiveSlider.setIsActive(false);
        inactiveSlider.setOrder(-1L);

        when(sliderRepository.findById("sliderId")).thenReturn(Optional.of(inactiveSlider));
        when(sliderRepository.save(any(Slider.class))).thenReturn(inactiveSlider);
        when(sliderMapper.toSliderDto(inactiveSlider)).thenReturn(sliderDto);

        // Act
        SliderDto result = sliderService.toggleActive("sliderId");

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