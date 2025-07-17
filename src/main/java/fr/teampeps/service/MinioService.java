package fr.teampeps.service;

import fr.teampeps.enums.Bucket;
import fr.teampeps.exceptions.UploadImageException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    public String uploadImageFromMultipartFile(MultipartFile file, String fileName, Bucket bucket) {
        try {
            String extension = extractExtension(file.getOriginalFilename());
            if(extension.isEmpty()) {
                log.warn("Le fichier {} n'a pas d'extension, on utilise .jpg par défaut", fileName);
                extension = ".jpg";
            } else {
                extension = extension.toLowerCase();
            }
            byte[] bytes = file.getBytes();

            if(isConversionNeeded(Objects.requireNonNull(extension))) {
                log.info("Conversion de l'image {} en webp", file.getOriginalFilename());
                bytes = convertToWebp(bytes, extension);
                extension = "webp";
            }
            return uploadImageFromBytes(bytes, fileName, extension, bucket);

        } catch (Exception e) {
            throw new UploadImageException("Error uploading image", e);
        }
    }

    String formatKey(String key) {
        if (key == null) return null;
        return key
                .toLowerCase()
                .replaceAll("[\\s'\\-]", "_");
    }

    String extractExtension(String originalFilename) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        return "";
    }

    public String uploadImageFromBytes(byte[] imageContent, String fileName, String extension, Bucket bucket) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageContent);
            String sanitizedExtension = extension.replace(".", "").toLowerCase();
            String key = formatKey(fileName) + "." + sanitizedExtension;

            log.info("Uploading file (from byte) to Minio: {}", key);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket.name().toLowerCase())
                            .object(key)
                            .stream(inputStream, imageContent.length, -1)
                            .contentType("image/" + extension)
                            .build()
            );

            return bucket.name().toLowerCase() + "/" + key;

        } catch (Exception e) {
            throw new UploadImageException("Error uploading image", e);
        }
    }

    public void deleteImage(String imageKey, Bucket bucket) {
        try {
            log.info("Deleting file from Minio: {}", imageKey);
            minioClient.removeObject(
                    io.minio.RemoveObjectArgs.builder()
                            .bucket(bucket.name().toLowerCase())
                            .object(imageKey)
                            .build()
            );
        } catch (Exception e) {
            throw new UploadImageException("Error deleting image", e);
        }
    }

    public byte[] convertToWebp(byte[] imageBytes, String extension) throws IOException {
        Path inputFile = Files.createTempFile("input_", "." + extension);
        Path outputFile = Files.createTempFile("output_", ".webp");

        Files.write(inputFile, imageBytes);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "cwebp",
                inputFile.toString(),
                "-q", "85",
                "-o", outputFile.toAbsolutePath().toString()
        );

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        try {
            if(!process.waitFor(10, TimeUnit.SECONDS)) {
                process.destroy();
                log.warn("Conversion de l'image a pris trop de temps, le processus a été détruit");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Conversion de l'image interrompue : {}", e.getMessage());
        }

        byte[] convertedBytes = Files.readAllBytes(outputFile);

        Files.deleteIfExists(inputFile);
        Files.deleteIfExists(outputFile);

        return convertedBytes;
    }

    public boolean isConversionNeeded(String name) {
        return name.matches(".*\\.(jpg|png)$");
    }
}
