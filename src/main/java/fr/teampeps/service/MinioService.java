package fr.teampeps.service;

import fr.teampeps.model.Bucket;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    public String uploadImageFromMultipartFile(MultipartFile file, String fileName, Bucket bucket) {
        try {
            String extension = "";
            extension = extractExtension(file.getOriginalFilename());
            String key = formatKey(fileName) + extension;

            log.info("Uploading file (from multipartfile) to Minio: {}", key);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket.name().toLowerCase())
                            .object(key)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return bucket.name().toLowerCase() + "/" + key;

        } catch (Exception e) {
            throw new RuntimeException("Error uploading image", e);
        }
    }

    private String formatKey(String key) {
        if (key == null) return null;
        return key
                .toLowerCase()
                .replaceAll("[\\s'\\-]", "_");
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        return "";
    }

    public String uploadImageFromBytes(byte[] imageContent, String fileName, String extension, Bucket bucket) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageContent);
            String key = formatKey(fileName) + extension;

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
            throw new RuntimeException("Error uploading image", e);
        }
    }
}
