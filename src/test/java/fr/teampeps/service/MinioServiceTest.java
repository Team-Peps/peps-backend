package fr.teampeps.service;

import fr.teampeps.enums.Bucket;
import fr.teampeps.exceptions.UploadImageException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
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

import java.io.BufferedInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioService minioService;

    @Captor
    private ArgumentCaptor<PutObjectArgs> putObjectArgsCaptor;

    private MultipartFile jpgFile;
    private MultipartFile pngFile;
    private MultipartFile fileWithoutExtension;
    private byte[] imageBytes;

    @BeforeEach
    void setUp() {
        // Setup test data
        jpgFile = new MockMultipartFile(
                "image.jpg",
                "test_image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        pngFile = new MockMultipartFile(
                "image.png",
                "test_image.png",
                "image/png",
                "test image content".getBytes()
        );

        fileWithoutExtension = new MockMultipartFile(
                "image",
                "test_image",
                "image/jpeg",
                "test image content".getBytes()
        );

        imageBytes = "test image content".getBytes();
    }

    @Test
    void uploadImageFromMultipartFile_WithJpgExtension_Success() throws Exception {
        // Arrange
        String fileName = "test_file";
        Bucket bucket = Bucket.MEMBERS;

        // Act
        String result = minioService.uploadImageFromMultipartFile(jpgFile, fileName, bucket);

        // Assert
        verify(minioClient).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs capturedArgs = putObjectArgsCaptor.getValue();

        assertEquals("members/test_file.jpg", result);
        assertEquals("members", capturedArgs.bucket());
        assertEquals("test_file.jpg", capturedArgs.object());
        assertEquals("image/jpeg", capturedArgs.contentType());
    }

    @Test
    void uploadImageFromMultipartFile_WithPngExtension_Success() throws Exception {
        // Arrange
        String fileName = "test_file";
        Bucket bucket = Bucket.MEMBERS;

        // Act
        String result = minioService.uploadImageFromMultipartFile(pngFile, fileName, bucket);

        // Assert
        verify(minioClient).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs capturedArgs = putObjectArgsCaptor.getValue();

        assertEquals("members/test_file.png", result);
        assertEquals("members", capturedArgs.bucket());
        assertEquals("test_file.png", capturedArgs.object());
        assertEquals("image/png", capturedArgs.contentType());
    }

    @Test
    void uploadImageFromMultipartFile_WithoutExtension_Success() throws Exception {
        // Arrange
        String fileName = "test_file";
        Bucket bucket = Bucket.MEMBERS;

        // Act
        String result = minioService.uploadImageFromMultipartFile(fileWithoutExtension, fileName, bucket);

        // Assert
        verify(minioClient).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs capturedArgs = putObjectArgsCaptor.getValue();

        assertEquals("members/test_file", result);
        assertEquals("members", capturedArgs.bucket());
        assertEquals("test_file", capturedArgs.object());
        assertEquals("image/jpeg", capturedArgs.contentType());
    }

    @Test
    void uploadImageFromMultipartFile_WithSpecialCharsInFileName_Success() throws Exception {
        // Arrange
        String fileName = "test file-with'special-chars";
        Bucket bucket = Bucket.MEMBERS;

        // Act
        String result = minioService.uploadImageFromMultipartFile(jpgFile, fileName, bucket);

        // Assert
        verify(minioClient).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs capturedArgs = putObjectArgsCaptor.getValue();

        assertEquals("members/test_file_with_special_chars.jpg", result);
        assertEquals("members", capturedArgs.bucket());
        assertEquals("test_file_with_special_chars.jpg", capturedArgs.object());
    }

    @Test
    void uploadImageFromMultipartFile_WithNullFileName_Success() throws Exception {
        // Arrange
        String fileName = null;
        Bucket bucket = Bucket.MEMBERS;

        // Act
        String result = minioService.uploadImageFromMultipartFile(jpgFile, fileName, bucket);

        // Assert
        verify(minioClient).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs capturedArgs = putObjectArgsCaptor.getValue();

        assertEquals("members/null.jpg", result);
        assertEquals("members", capturedArgs.bucket());
        assertEquals("null.jpg", capturedArgs.object());
    }

    @Test
    void uploadImageFromMultipartFile_MinioClientThrowsException() throws Exception {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("image.png");
        when(file.getInputStream()).thenThrow(new IOException("InputStream failed"));

        String fileName = "testImage";
        Bucket bucket = Bucket.MEMBERS;

        // Act & Assert
        UploadImageException exception = assertThrows(UploadImageException.class, () ->
                minioService.uploadImageFromMultipartFile(file, fileName, bucket));

        assertEquals("Error uploading image", exception.getMessage());
    }


    @Test
    void uploadImageFromBytes_Success() throws Exception {
        // Arrange
        String fileName = "test_file";
        String extension = ".jpg";
        Bucket bucket = Bucket.MEMBERS;

        // Act
        String result = minioService.uploadImageFromBytes(imageBytes, fileName, extension, bucket);

        // Assert
        verify(minioClient).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs capturedArgs = putObjectArgsCaptor.getValue();

        assertEquals("members/test_file.jpg", result);
        assertEquals("members", capturedArgs.bucket());
        assertEquals("test_file.jpg", capturedArgs.object());
        assertEquals("image/.jpg", capturedArgs.contentType());

        // Verify InputStream content
        assertInstanceOf(BufferedInputStream.class, capturedArgs.stream());
    }

    @Test
    void uploadImageFromBytes_WithSpecialCharsInFileName_Success() throws Exception {
        // Arrange
        String fileName = "test file-with'special-chars";
        String extension = ".png";
        Bucket bucket = Bucket.MEMBERS;

        // Act
        String result = minioService.uploadImageFromBytes(imageBytes, fileName, extension, bucket);

        // Assert
        verify(minioClient).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs capturedArgs = putObjectArgsCaptor.getValue();

        assertEquals("members/test_file_with_special_chars.png", result);
        assertEquals("members", capturedArgs.bucket());
        assertEquals("test_file_with_special_chars.png", capturedArgs.object());
    }

    @Test
    void uploadImageFromBytes_MinioClientThrowsException() throws Exception {
        // Arrange
        byte[] imageContent = "invalid".getBytes();
        String fileName = "fail";
        String extension = ".jpg";
        Bucket bucket = Bucket.MEMBERS;

        doThrow(new RuntimeException("MinIO failed")).when(minioClient)
                .putObject(any(PutObjectArgs.class));

        // Act & Assert
        UploadImageException exception = assertThrows(UploadImageException.class, () ->
                minioService.uploadImageFromBytes(imageContent, fileName, extension, bucket));

        assertEquals("Error uploading image", exception.getMessage());
    }

    @Test
    void formatKey_WithSpecialChars() {
        // Arrange
        MinioService spyService = spy(minioService);
        String input = "test file-with'special-chars";

        // Act
        String result = spyService.formatKey(input);

        // Assert
        assertEquals("test_file_with_special_chars", result);
    }

    @Test
    void formatKey_WithNull() {
        // Arrange
        MinioService spyService = spy(minioService);

        // Act
        String result = spyService.formatKey(null);

        // Assert
        assertNull(result);
    }

    @Test
    void extractExtension_WithValidExtension() {
        // Arrange
        MinioService spyService = spy(minioService);
        String filename = "test.jpg";

        // Act
        String result = spyService.extractExtension(filename);

        // Assert
        assertEquals(".jpg", result);
    }

    @Test
    void extractExtension_WithoutExtension() {
        // Arrange
        MinioService spyService = spy(minioService);
        String filename = "test";

        // Act
        String result = spyService.extractExtension(filename);

        // Assert
        assertEquals("", result);
    }

    @Test
    void extractExtension_WithNull() {
        // Arrange
        MinioService spyService = spy(minioService);

        // Act
        String result = spyService.extractExtension(null);

        // Assert
        assertEquals("", result);
    }
}