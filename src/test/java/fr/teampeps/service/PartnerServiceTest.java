package fr.teampeps.service;

import fr.teampeps.dto.PartnerDto;
import fr.teampeps.enums.PartnerType;
import fr.teampeps.mapper.PartnerMapper;
import fr.teampeps.enums.Bucket;
import fr.teampeps.models.Partner;
import fr.teampeps.repository.PartnerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerServiceTest {

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private PartnerMapper partnerMapper;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private PartnerService partnerService;

    private Partner partner;
    private PartnerDto partnerDto;
    private MultipartFile imageFile;
    private List<Partner> activePartners;
    private List<Partner> inactivePartners;

    @BeforeEach
    void setUp() {
        // Setup test data
        partner = new Partner();
        partner.setId("12345");
        partner.setName("TestPartner");
        partner.setIsActive(true);

        partnerDto = PartnerDto.builder().build();
        partnerDto.setId("12345");
        partnerDto.setName("TestPartner");

        // Create mock multipart file
        imageFile = new MockMultipartFile(
                "image.jpg",
                "image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Setup active and inactive partners
        Partner activePartner1 = new Partner();
        activePartner1.setId("active1");
        activePartner1.setName("ActivePartner1");
        activePartner1.setIsActive(true);

        Partner activePartner2 = new Partner();
        activePartner2.setId("active2");
        activePartner2.setName("ActivePartner2");
        activePartner2.setIsActive(true);

        Partner inactivePartner = new Partner();
        inactivePartner.setId("inactive1");
        inactivePartner.setName("InactivePartner");
        inactivePartner.setIsActive(false);

        activePartners = Arrays.asList(activePartner1, activePartner2);
        inactivePartners = Collections.singletonList(inactivePartner);
    }

    @Test
    void getAllPartners_Success() {
        // Arrange
        PartnerDto activePartnerDto1 = PartnerDto.builder().build();
        activePartnerDto1.setId("active1");
        activePartnerDto1.setName("ActivePartner1");

        PartnerDto activePartnerDto2 = PartnerDto.builder().build();
        activePartnerDto2.setId("active2");
        activePartnerDto2.setName("ActivePartner2");

        PartnerDto inactivePartnerDto = PartnerDto.builder().build();
        inactivePartnerDto.setId("inactive1");
        inactivePartnerDto.setName("InactivePartner");

        when(partnerRepository.findAllByIsActive(true)).thenReturn(activePartners);
        when(partnerRepository.findAllByIsActive(false)).thenReturn(inactivePartners);

        when(partnerMapper.toPartnerDto(activePartners.get(0))).thenReturn(activePartnerDto1);
        when(partnerMapper.toPartnerDto(activePartners.get(1))).thenReturn(activePartnerDto2);
        when(partnerMapper.toPartnerDto(inactivePartners.get(0))).thenReturn(inactivePartnerDto);

        // Act
        Map<String, List<PartnerDto>> result = partnerService.getAllPartners();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get("activePartners").size());
        assertEquals(1, result.get("inactivePartners").size());
        assertEquals("active1", result.get("activePartners").get(0).getId());
        assertEquals("active2", result.get("activePartners").get(1).getId());
        assertEquals("inactive1", result.get("inactivePartners").get(0).getId());
    }

    @Test
    void getAllPartners_EmptyLists() {
        // Arrange
        when(partnerRepository.findAllByIsActive(true)).thenReturn(Collections.emptyList());
        when(partnerRepository.findAllByIsActive(false)).thenReturn(Collections.emptyList());

        // Act
        Map<String, List<PartnerDto>> result = partnerService.getAllPartners();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get("activePartners").isEmpty());
        assertTrue(result.get("inactivePartners").isEmpty());
    }
/*
    @Test
    void savePartner_Success() {
        // Arrange
        when(minioService.uploadImageFromMultipartFile(any(MultipartFile.class), eq("testpartner"), eq(Bucket.PARTNERS)))
                .thenReturn("test-image-url");
        when(partnerRepository.save(any(Partner.class))).thenReturn(partner);
        when(partnerMapper.toPartnerDto(partner)).thenReturn(partnerDto);

        // Act
        PartnerDto result = partnerService.savePartner(partner, imageFile);

        // Assert
        assertNotNull(result);
        assertEquals(partnerDto, result);
        assertEquals("test-image-url", partner.getImageKey());
        verify(partnerRepository).save(partner);
        verify(minioService).uploadImageFromMultipartFile(imageFile, "testpartner", Bucket.PARTNERS);
    }

    @Test
    void savePartner_NullImageFile() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                partnerService.savePartner(partner, null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Image non fournie", exception.getReason());
    }

    @Test
    void savePartner_MinioServiceException() {
        // Arrange
        when(minioService.uploadImageFromMultipartFile(any(), any(), any()))
                .thenThrow(new RuntimeException("Error uploading image"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                partnerService.savePartner(partner, imageFile));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Erreur lors de la mise à jour du partnenaire", exception.getReason());
    }

    @Test
    void updatePartner_Success() {
        // Arrange
        when(minioService.uploadImageFromMultipartFile(any(MultipartFile.class), eq("testpartner"), eq(Bucket.PARTNERS)))
                .thenReturn("test-image-url");
        when(partnerRepository.save(any(Partner.class))).thenReturn(partner);
        when(partnerMapper.toPartnerDto(partner)).thenReturn(partnerDto);

        // Act
        PartnerDto result = partnerService.updatePartner(partner, imageFile);

        // Assert
        assertNotNull(result);
        assertEquals(partnerDto, result);
        assertEquals("test-image-url", partner.getImageKey());
        verify(partnerRepository).save(partner);
        verify(minioService).uploadImageFromMultipartFile(imageFile, "testpartner", Bucket.PARTNERS);
    }

    @Test
    void updatePartner_MinioServiceException() {
        // Arrange
        when(minioService.uploadImageFromMultipartFile(any(), any(), any()))
                .thenThrow(new RuntimeException("Error uploading image"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                partnerService.updatePartner(partner, imageFile));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Erreur lors de la mise à jour du partnenaire", exception.getReason());
    }*/

    @Test
    void deletePartner_Success() {
        // Act
        partnerService.deletePartner("12345");

        // Assert
        verify(partnerRepository).deleteById("12345");
    }

    @Test
    void deletePartner_NotFound() {
        // Arrange
        doThrow(new EntityNotFoundException("Partner not found")).when(partnerRepository).deleteById("nonexistent");

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                partnerService.deletePartner("nonexistent"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Partenaire non trouvé", exception.getReason());
    }

    @Test
    void toggleActive_Success() {
        // Arrange
        when(partnerRepository.findById("12345")).thenReturn(Optional.of(partner));
        when(partnerRepository.save(partner)).thenReturn(partner);
        when(partnerMapper.toPartnerDto(partner)).thenReturn(partnerDto);

        // Act
        PartnerDto result = partnerService.toggleActive("12345");

        // Assert
        assertNotNull(result);
        assertFalse(partner.getIsActive());
        verify(partnerRepository).save(partner);
    }

    @Test
    void toggleActive_PartnerNotFound() {
        // Arrange
        when(partnerRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                partnerService.toggleActive("nonexistent"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Partenaire non trouvé", exception.getReason());
    }

    @Test
    void getAllActivePartners_Success() {
        // Arrange
        PartnerDto activePartnerDto1 = PartnerDto.builder().build();
        activePartnerDto1.setId("active1");
        activePartnerDto1.setName("ActivePartner1");

        PartnerDto activePartnerDto2 = PartnerDto.builder().build();
        activePartnerDto2.setId("active2");
        activePartnerDto2.setName("ActivePartner2");

        when(partnerRepository.findAllByIsActiveAndPartnerType(true, PartnerType.MAJOR)).thenReturn(activePartners);
        when(partnerMapper.toPartnerDto(activePartners.get(0))).thenReturn(activePartnerDto1);
        when(partnerMapper.toPartnerDto(activePartners.get(1))).thenReturn(activePartnerDto2);

        // Act
        Map<String, List<PartnerDto>> result = partnerService.getAllActivePartners();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("active1", result.get(PartnerType.MAJOR.name()).get(0).getId());
        assertEquals("active2", result.get(PartnerType.MAJOR.name()).get(1).getId());
    }

    @Test
    void getAllActivePartners_EmptyList() {
        // Arrange
        when(partnerRepository.findAllByIsActiveAndPartnerType(true, PartnerType.MAJOR)).thenReturn(Collections.emptyList());

        // Act
        Map<String, List<PartnerDto>> result = partnerService.getAllActivePartners();

        // Assert
        assertNotNull(result);
        assertTrue(result.get(PartnerType.MAJOR.name()).isEmpty());
    }
}