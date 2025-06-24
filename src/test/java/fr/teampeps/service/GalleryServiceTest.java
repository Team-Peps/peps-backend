package fr.teampeps.service;

import fr.teampeps.dto.GalleryDto;
import fr.teampeps.dto.GalleryTinyDto;
import fr.teampeps.enums.Bucket;
import fr.teampeps.mapper.GalleryMapper;
import fr.teampeps.models.Author;
import fr.teampeps.models.Gallery;
import fr.teampeps.models.GalleryPhoto;
import fr.teampeps.record.GalleryRequest;
import fr.teampeps.record.GalleryTranslationRequest;
import fr.teampeps.repository.GalleryPhotoRepository;
import fr.teampeps.repository.GalleryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GalleryServiceTest {

    @Mock
    private GalleryRepository galleryRepository;

    @Mock
    private GalleryPhotoRepository galleryPhotoRepository;

    @Mock
    private GalleryMapper galleryMapper;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private GalleryService galleryService;

    @Captor
    private ArgumentCaptor<Gallery> galleryCaptor;

    private Gallery gallery;
    private GalleryDto galleryDto;
    private GalleryTinyDto galleryTinyDto;
    private final String GALLERY_ID = "gallery-id-123";
    private final Author author = new Author();
    private GalleryRequest galleryRequest;

    @BeforeEach
    void setUp() {
        gallery = new Gallery();
        gallery.setId(GALLERY_ID);
        gallery.setDate(LocalDate.now());
        gallery.setPhotos(new ArrayList<>());

        galleryDto = GalleryDto.builder().build();
        galleryDto.setId(GALLERY_ID);
        galleryDto.setDate(String.valueOf(LocalDate.now()));
        galleryDto.setPhotos(new ArrayList<>());

        galleryTinyDto = GalleryTinyDto.builder().build();
        galleryTinyDto.setId(GALLERY_ID);
        galleryTinyDto.setDate(String.valueOf(LocalDate.now()));

        author.setId("author-id-123");
        author.setName("Test Author");

        galleryRequest = new GalleryRequest(
                LocalDate.now(),
                "image-key",
                Map.of(
                        "fr", new GalleryTranslationRequest("Evénement de test", "Description en français"),
                        "en", new GalleryTranslationRequest("Test Event", "Description in English")
                )
        );

    }

    @Test
    void createGallery_Success() {
        when(galleryRepository.existsById(anyString())).thenReturn(false);
        when(galleryRepository.save(any(Gallery.class))).thenReturn(gallery);
        when(galleryMapper.toGalleryTinyDto(any(Gallery.class))).thenReturn(galleryTinyDto);
        when(galleryMapper.toGallery(any(GalleryRequest.class))).thenReturn(gallery);
        MultipartFile imageFile = mock(MultipartFile.class);

        GalleryTinyDto result = galleryService.createGallery(galleryRequest, imageFile);
        assertEquals(galleryTinyDto, result);
    }

    @Test
    void createGallery_DuplicateEventName_ThrowsException() {
        when(galleryRepository.existsById(anyString())).thenReturn(true);
        MultipartFile imageFile = mock(MultipartFile.class);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> galleryService.createGallery(galleryRequest, imageFile)
        );

        assertEquals("Une galerie avec cet ID existe déjà", exception.getMessage());
        verify(galleryRepository, never()).save(any(Gallery.class));
    }

    @Test
    void getAllGallery_ReturnsAllGalleries() {
        List<Gallery> galleries = new ArrayList<>();
        galleries.add(gallery);

        when(galleryRepository.findAllOrderByDate()).thenReturn(galleries);
        when(galleryMapper.toGalleryDto(any(Gallery.class))).thenReturn(galleryDto);

        List<GalleryDto> result = galleryService.getAllGallery();

        verify(galleryRepository).findAllOrderByDate();
        assertEquals(1, result.size());
        assertEquals(galleryDto, result.get(0));
    }

    @Test
    void updateGallery_Success() {
        GalleryDto updatedDto = GalleryDto.builder().build();
        updatedDto.setId(GALLERY_ID);
        updatedDto.setDate(String.valueOf(LocalDate.now().plusDays(1)));
        MultipartFile imageFile = mock(MultipartFile.class);

        when(galleryRepository.findById(GALLERY_ID)).thenReturn(Optional.of(gallery));
        when(galleryRepository.save(any(Gallery.class))).thenReturn(gallery);
        when(galleryMapper.toGalleryDto(any(Gallery.class))).thenReturn(updatedDto);

        GalleryDto result = galleryService.updateGallery(GALLERY_ID, galleryRequest, imageFile);

        verify(galleryRepository).findById(GALLERY_ID);
        assertEquals(updatedDto, result);
    }

    @Test
    void updateGallery_GalleryNotFound_ThrowsException() {
        when(galleryRepository.findById(GALLERY_ID)).thenReturn(Optional.empty());
        MultipartFile imageFile = mock(MultipartFile.class);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> galleryService.updateGallery(GALLERY_ID, galleryRequest, imageFile)
        );

        assertEquals("Aucune galerie trouvée avec cet ID", exception.getMessage());
        verify(galleryRepository).findById(GALLERY_ID);
        verify(galleryRepository, never()).save(any(Gallery.class));
    }

    // Tests for deleteGallery method
    @Test
    void deleteGallery_Success() {
        GalleryPhoto photo1 = new GalleryPhoto();
        photo1.setImageKey("key1");
        photo1.setGallery(gallery);

        GalleryPhoto photo2 = new GalleryPhoto();
        photo2.setImageKey("key2");
        photo2.setGallery(gallery);

        gallery.setPhotos(new ArrayList<>(Arrays.asList(photo1, photo2)));

        when(galleryRepository.findById(GALLERY_ID)).thenReturn(Optional.of(gallery));

        galleryService.deleteGallery(GALLERY_ID);

        verify(galleryRepository).findById(GALLERY_ID);
        verify(minioService).deleteImage("key1", Bucket.GALLERIES);
        verify(minioService).deleteImage("key2", Bucket.GALLERIES);
        verify(galleryRepository).delete(gallery);
    }

    @Test
    void deleteGallery_GalleryNotFound_ThrowsException() {
        when(galleryRepository.findById(GALLERY_ID)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> galleryService.deleteGallery(GALLERY_ID)
        );

        assertEquals("Aucune galerie trouvée avec cet ID", exception.getMessage());
        verify(galleryRepository).findById(GALLERY_ID);
        verify(minioService, never()).deleteImage(anyString(), any(Bucket.class));
        verify(galleryRepository, never()).delete(any(Gallery.class));
    }

    // Tests for deletePhoto method
    @Test
    void deletePhoto_Success() {
        String photoId = "photo-id-123";
        GalleryPhoto photo = new GalleryPhoto();
        photo.setId(photoId);
        photo.setImageKey("photo-key");
        photo.setGallery(gallery);

        gallery.setPhotos(new ArrayList<>(Collections.singletonList(photo)));

        when(galleryPhotoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        galleryService.deletePhoto(photoId);

        verify(galleryPhotoRepository).findById(photoId);
        verify(minioService).deleteImage("photo-key", Bucket.GALLERIES);
        verify(galleryRepository).save(gallery);
        assertTrue(gallery.getPhotos().isEmpty());
    }

    @Test
    void deletePhoto_PhotoNotFound_ThrowsException() {
        String photoId = "photo-id-123";
        when(galleryPhotoRepository.findById(photoId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> galleryService.deletePhoto(photoId)
        );

        assertEquals("Aucune photo trouvée avec cet ID", exception.getMessage());
        verify(galleryPhotoRepository).findById(photoId);
        verify(minioService, never()).deleteImage(anyString(), any(Bucket.class));
        verify(galleryRepository, never()).save(any(Gallery.class));
    }

    @Test
    void addPhotosToGallery_GalleryNotFound_ThrowsException() {
        when(galleryRepository.findById(GALLERY_ID)).thenReturn(Optional.empty());
        MockMultipartFile zipFile = new MockMultipartFile("file", "test.zip", "application/zip", new byte[]{});

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> galleryService.addPhotosToGallery(GALLERY_ID, zipFile, author)
        );

        assertEquals("Aucune galerie trouvée avec cet ID", exception.getMessage());
        verify(galleryRepository).findById(GALLERY_ID);
        verify(minioService, never()).uploadImageFromBytes(any(), anyString(), anyString(), any(Bucket.class));
    }

    @Test
    void addPhotosToGallery_EmptyZipFile_ThrowsException() {
        when(galleryRepository.findById(GALLERY_ID)).thenReturn(Optional.of(gallery));
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);
            zos.close();
        } catch (IOException e) {
            fail("Erreur lors de la création du fichier ZIP de test");
        }

        MockMultipartFile zipFile = new MockMultipartFile("file", "test.zip", "application/zip", baos.toByteArray());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> galleryService.addPhotosToGallery(GALLERY_ID, zipFile, author)
        );

        assertEquals("Aucune photo trouvée dans le fichier zip", exception.getMessage());
    }

    @Test
    void addPhotosToGallery_NullZipFile_ThrowsException() {
        when(galleryRepository.findById(GALLERY_ID)).thenReturn(Optional.of(gallery));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> galleryService.addPhotosToGallery(GALLERY_ID, null, author)
        );

        assertEquals("Aucun fichier zip fourni", exception.getMessage());
    }

    @Test
    void addPhotosToGallery_ZipWithNoImages_ThrowsException() throws IOException {
        when(galleryRepository.findById(GALLERY_ID)).thenReturn(Optional.of(gallery));

        // Create zip with no valid images
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        // Add a text file (not an image)
        ZipEntry textEntry = new ZipEntry("test.txt");
        zos.putNextEntry(textEntry);
        zos.write("This is not an image".getBytes());
        zos.closeEntry();

        zos.close();

        MockMultipartFile zipFile = new MockMultipartFile("file", "test.zip", "application/zip", baos.toByteArray());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> galleryService.addPhotosToGallery(GALLERY_ID, zipFile, author)
        );

        assertEquals("Aucune photo trouvée dans le fichier zip", exception.getMessage());
    }

    @Test
    void addPhotosToGallery_WebpImage_NoConversionNeeded() throws IOException {
        when(galleryRepository.findById(GALLERY_ID)).thenReturn(Optional.of(gallery));

        // Create a test zip with one webp image (no conversion needed)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        ZipEntry webpEntry = new ZipEntry("test.webp");
        zos.putNextEntry(webpEntry);
        zos.write(new byte[]{1, 2, 3, 4}); // Dummy image data
        zos.closeEntry();

        zos.close();

        MockMultipartFile zipFile = new MockMultipartFile("file", "test.zip", "application/zip", baos.toByteArray());

        when(minioService.uploadImageFromBytes(any(), eq("test"), eq("webp"), any(Bucket.class)))
                .thenReturn("webp-image-key");

        when(galleryRepository.save(any(Gallery.class))).thenReturn(gallery);
        when(galleryMapper.toGalleryDto(any(Gallery.class))).thenReturn(galleryDto);

        GalleryDto result = galleryService.addPhotosToGallery(GALLERY_ID, zipFile, author);

        verify(galleryRepository).save(galleryCaptor.capture());
        Gallery savedGallery = galleryCaptor.getValue();
        assertEquals(1, savedGallery.getPhotos().size());

        // Verify no conversion was attempted (we only check the upload method was called with webp extension)
        verify(minioService).uploadImageFromBytes(any(), eq("test"), eq("webp"), eq(Bucket.GALLERIES));
    }

    // Helper methods
    private byte[] createTestZipWithImages() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        // Add a jpg file
        ZipEntry jpgEntry = new ZipEntry("test.jpg");
        zos.putNextEntry(jpgEntry);
        zos.write(new byte[]{1, 2, 3, 4}); // Dummy image data
        zos.closeEntry();

        // Add a png file
        ZipEntry pngEntry = new ZipEntry("test2.png");
        zos.putNextEntry(pngEntry);
        zos.write(new byte[]{5, 6, 7, 8}); // Dummy image data
        zos.closeEntry();

        zos.close();
        return baos.toByteArray();
    }

}