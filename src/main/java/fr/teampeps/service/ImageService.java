package fr.teampeps.service;

import fr.teampeps.model.Image;
import fr.teampeps.repository.ImageRepository;
import fr.teampeps.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public String uploadImage(MultipartFile imageFile) throws IOException {
        Image image = Image.builder()
                .name(imageFile.getOriginalFilename())
                .type(imageFile.getContentType())
                .imageData(ImageUtils.compressImage(imageFile.getBytes()))
                .build();
        imageRepository.save(image);
        return image.getName();
    }

    public byte[] downloadImage(Long id) {
        Optional<Image> image = imageRepository.findById(id);
        return image.map(img -> {
            try {
                return ImageUtils.decompressImage(img.getImageData());
            } catch (DataFormatException | IOException e) {
                throw new RuntimeException(e);
            }
        }).orElse(null);

    }

}
