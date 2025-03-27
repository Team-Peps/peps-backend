package fr.teampeps.service;

import fr.teampeps.model.Bucket;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    public String uploadImage(MultipartFile file, String fileName, Bucket bucket) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String key = fileName.replace(" ", "_") + extension;
            log.info("Uploading file to Minio: {}", key);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket.name())
                            .object(key)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return key;

        } catch (Exception e) {
            throw new RuntimeException("Error uploading image", e);
        }
    }

}
