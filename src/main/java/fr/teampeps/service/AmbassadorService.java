package fr.teampeps.service;

import fr.teampeps.dto.AmbassadorDto;
import fr.teampeps.mapper.AmbassadorMapper;
import fr.teampeps.models.Ambassador;
import fr.teampeps.enums.Bucket;
import fr.teampeps.repository.AmbassadorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmbassadorService {

    private final AmbassadorRepository ambassadorRepository;
    private final MinioService minioService;
    private final AmbassadorMapper ambassadorMapper;

    public AmbassadorDto saveOrUpdateAmbassador(Ambassador ambassador, MultipartFile imageFile) {

        if(imageFile == null || imageFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucune image fournie");
        }

        try {
            String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, ambassador.getName().toLowerCase(), Bucket.AMBASSADORS);
            ambassador.setImageKey(imageUrl);

            return ambassadorMapper.toAmbassadorDto(ambassadorRepository.save(ambassador));

        } catch (Exception e) {
            log.error("Error saving ambassador with ID: {}", ambassador.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour de l'ambassadeur", e);
        }
    }

    public List<AmbassadorDto> getAllAmbassadors() {
        return ambassadorRepository.findAll().stream()
                .map(ambassadorMapper::toAmbassadorDto)
                .toList();
    }

    public void deleteAmbassador(String id) {
        try {
            ambassadorRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ambassadeur non trouvé", e);
        }
    }
}
