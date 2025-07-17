package fr.teampeps.service;

import fr.teampeps.dto.GalleryDto;
import fr.teampeps.dto.GalleryTinyDto;
import fr.teampeps.enums.Bucket;
import fr.teampeps.mapper.GalleryMapper;
import fr.teampeps.models.Author;
import fr.teampeps.models.Gallery;
import fr.teampeps.models.GalleryPhoto;
import fr.teampeps.models.GalleryTranslation;
import fr.teampeps.record.GalleryRequest;
import fr.teampeps.repository.GalleryPhotoRepository;
import fr.teampeps.repository.GalleryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    public GalleryTinyDto createGallery(
            GalleryRequest galleryRequest,
            MultipartFile imageFile
    ) {

        String eventNameFr = galleryRequest.translations().get("fr").eventName();
        String id = eventNameFr.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "-") + "-" + galleryRequest.date();

        if(imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Aucune image fournie");
        }
        if(galleryRepository.existsById(id)) {
            throw new IllegalArgumentException("Une galerie avec cet ID existe déjà");
        }
        try {
            Gallery gallery = galleryMapper.toGallery(galleryRequest);
            gallery.setId(id);

            String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, id, Bucket.GALLERIES);
            gallery.setThumbnailImageKey(imageUrl);

            List<GalleryTranslation> validTranslations = gallery.getTranslations().stream()
                    .filter(t -> t.getLang() != null && !t.getLang().isBlank() && t.getEventName() != null && !t.getEventName().isBlank())
                    .peek(translation -> translation.setParent(gallery))
                    .toList();
            gallery.setTranslations(validTranslations);

            return galleryMapper.toGalleryTinyDto(galleryRepository.save(gallery));
        } catch (Exception e) {
            log.error("Error saving gallery with ID: {}", eventNameFr, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la sauvegarde de la galerie", e);
        }

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

    public GalleryDto updateGallery(
            String galleryId,
            GalleryRequest galleryRequest,
            MultipartFile imageFile
    ) {

        Gallery existingGallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new IllegalArgumentException("Aucune galerie trouvée avec cet ID"));

        try {
            Map<String, GalleryTranslation> translationsByLang = existingGallery.getTranslations().stream()
                    .collect(Collectors.toMap(GalleryTranslation::getLang, Function.identity()));

            galleryRequest.translations().forEach((lang, tRequest) -> {
                GalleryTranslation translation = translationsByLang.get(lang.toLowerCase());
                if(translation != null) {
                    translation.setDescription(tRequest.description());
                    translation.setEventName(tRequest.eventName());
                }
            });

            if(imageFile != null) {
                String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, galleryId, Bucket.GALLERIES);
                existingGallery.setThumbnailImageKey(imageUrl);
            }

            existingGallery.setDate(galleryRequest.date());

            return galleryMapper.toGalleryDto(galleryRepository.save(existingGallery));

        } catch (Exception e) {
            log.error("Error saving gallery with ID: {}", galleryId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour de la galerie", e);
        }
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

    public List<GalleryTinyDto> getClubGalleries() {
        return galleryRepository.findAll().stream()
                .map(galleryMapper::toGalleryTinyDto)
                .toList();
    }

    public GalleryDto getGalleryById(String galleryId) {
        return galleryRepository.findById(galleryId)
                .map(galleryMapper::toGalleryDto)
                .orElseThrow(() -> new IllegalArgumentException("Aucune galerie trouvée avec cet ID"));
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

                    if(minioService.isConversionNeeded(zipEntry.getName())){
                        imageBytes = minioService.convertToWebp(imageBytes, extension);
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

    private boolean isImage(String name) {
        return name.matches(".*\\.(webp|avif|jpg|png)$");
    }
}
