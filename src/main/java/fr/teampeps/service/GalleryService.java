package fr.teampeps.service;

import fr.teampeps.dto.GalleryDto;
import fr.teampeps.dto.GalleryTinyDto;
import fr.teampeps.enums.Bucket;
import fr.teampeps.mapper.GalleryMapper;
import fr.teampeps.models.Author;
import fr.teampeps.models.Gallery;
import fr.teampeps.models.GalleryPhoto;
import fr.teampeps.repository.GalleryPhotoRepository;
import fr.teampeps.repository.GalleryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final GalleryPhotoRepository galleryPhotoRepository;
    private final GalleryMapper galleryMapper;
    private final MinioService minioService;

    public GalleryDto addPhotosToGallery(String galleryId, MultipartFile zipFile, Author author) {

        Optional<Gallery> galleryOptional = galleryRepository.findById(galleryId);
        Gallery gallery;

        if(galleryOptional.isEmpty()) {
            throw new IllegalArgumentException("Aucune galerie trouvée avec cet ID");
        }

        gallery = galleryOptional.get();

        if(zipFile == null || zipFile.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier zip fourni");
        }

        List<GalleryPhoto> photos = extractAndSavePhotosFromZip(zipFile, gallery, author);

        if(photos.isEmpty()) {
            throw new IllegalArgumentException("Aucune photo trouvée dans le fichier zip");
        }

        gallery.getPhotos().addAll(photos);
        return galleryMapper.toGalleryDto(galleryRepository.save(gallery));
    }

    private List<GalleryPhoto> extractAndSavePhotosFromZip(MultipartFile zipFile, Gallery gallery, Author author) {

        List<GalleryPhoto> photos = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry zipEntry;

            while((zipEntry = zis.getNextEntry()) != null) {
                if(!zipEntry.isDirectory() && isImage(zipEntry.getName())) {

                    log.info("Traitement de l'image : {}", zipEntry.getName());

                    byte[] imageBytes = zis.readAllBytes();
                    String originalName = Paths.get(zipEntry.getName()).getFileName().toString();
                    String extension = originalName.substring(originalName.lastIndexOf('.') + 1);

                    String baseName = originalName.contains(".")
                            ? originalName.substring(0, originalName.lastIndexOf('.'))
                            : originalName;

                    if(isConversionNeeded(zipEntry.getName())){
                        imageBytes = convertToWebp(imageBytes, extension);
                        extension = "webp";
                    }

                    String imageKey = minioService.uploadImageFromBytes(imageBytes, baseName, extension, Bucket.GALLERIES);

                    GalleryPhoto photo = new GalleryPhoto();
                    photo.setImageKey(imageKey);
                    photo.setGallery(gallery);
                    photo.setAuthor(author);

                    photos.add(photo);
                }
            }
            zis.closeEntry();
            log.info("Extraction terminée, {} images trouvées", photos.size());

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'extraction du fichier zip", e);
        }

        return photos;
    }

    private byte[] convertToWebp(byte[] imageBytes, String extension) throws IOException {
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

    public GalleryDto createGallery(Gallery gallery) {
        if(galleryRepository.existsByEventName(gallery.getEventName())) {
            throw new IllegalArgumentException("Une galerie avec ce nom d'événement existe déjà");
        }

        return galleryMapper.toGalleryDto(galleryRepository.save(gallery));
    }

    private boolean isConversionNeeded(String name) {
        return name.matches(".*\\.(jpg|png)$");
    }

    private boolean isImage(String name) {
        return name.matches(".*\\.(webp|avif|jpg|png)$");
    }

    public void deleteGallery(String galleryId) {
        Optional<Gallery> galleryOptional = galleryRepository.findById(galleryId);

        if(galleryOptional.isPresent()) {
            Gallery gallery = galleryOptional.get();
            gallery.getPhotos().forEach(photo -> minioService.deleteImage(photo.getImageKey(), Bucket.GALLERIES));
            galleryRepository.delete(gallery);
        } else {
            throw new IllegalArgumentException("Aucune galerie trouvée avec cet ID");
        }
    }

    public List<GalleryDto> getAllGallery() {
        List<Gallery> galleries = galleryRepository.findAllOrderByDate();
        return galleries.stream()
                .map(galleryMapper::toGalleryDto)
                .toList();
    }

    public GalleryDto updateGallery(String galleryId, Gallery gallery) {
        Gallery existingGallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new IllegalArgumentException("Aucune galerie trouvée avec cet ID"));

            existingGallery.setEventName(gallery.getEventName());
            existingGallery.setDate(gallery.getDate());
            existingGallery.setDescription(gallery.getDescription());
            return galleryMapper.toGalleryDto(galleryRepository.save(existingGallery));
    }

    public void deletePhoto(String photoId) {
        Optional<GalleryPhoto> photoOptional = galleryPhotoRepository.findById(photoId);

        if(photoOptional.isPresent()) {
            GalleryPhoto photo = photoOptional.get();
            Gallery gallery = photo.getGallery();

            gallery.getPhotos().remove(photo);

            minioService.deleteImage(photo.getImageKey(), Bucket.GALLERIES);
            galleryRepository.save(gallery);
        } else {
            throw new IllegalArgumentException("Aucune photo trouvée avec cet ID");
        }
    }

    public Page<GalleryTinyDto> getGalleries(int page) {
        Pageable pageable = PageRequest.of(page, 9, Sort.by(Sort.Direction.DESC, "date"));

        return galleryRepository.findAll(pageable).map(galleryMapper::toGalleryTinyDto);
    }
}
