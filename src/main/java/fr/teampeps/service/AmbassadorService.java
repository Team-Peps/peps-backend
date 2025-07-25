package fr.teampeps.service;

import fr.teampeps.dto.AmbassadorDto;
import fr.teampeps.mapper.AmbassadorMapper;
import fr.teampeps.models.Ambassador;
import fr.teampeps.enums.Bucket;
import fr.teampeps.models.AmbassadorTranslation;
import fr.teampeps.record.AmbassadorRequest;
import fr.teampeps.repository.AmbassadorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class AmbassadorService {

    private final AmbassadorRepository ambassadorRepository;
    private final MinioService minioService;
    private final AmbassadorMapper ambassadorMapper;

    public AmbassadorDto saveAmbassador(
            AmbassadorRequest ambassadorRequest,
            MultipartFile imageFile
            ) {
        Ambassador ambassador = ambassadorMapper.toAmbassador(ambassadorRequest);

        try {
            String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, ambassador.getName().toLowerCase(), Bucket.AMBASSADORS);
            ambassador.setImageKey(imageUrl);
        } catch (Exception e) {
            log.error("Error uploading image for ambassador: {}", ambassador.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de l'upload de l'image de l'ambassadeur", e);
        }

        List<AmbassadorTranslation> validTranslations = ambassador.getTranslations().stream()
                .filter(t -> t.getLang() != null && !t.getLang().isBlank() && t.getDescription() != null && !t.getDescription().isBlank())
                .peek(ambassadorTranslation -> ambassadorTranslation.setParent(ambassador))
                .toList();
        ambassador.setTranslations(validTranslations);

        return ambassadorMapper.toAmbassadorDto(ambassadorRepository.save(ambassador));
    }

    public AmbassadorDto updateAmbassador(
            AmbassadorRequest ambassadorRequest,
            MultipartFile imageFile
    ) {
        Ambassador existingAmbassador = ambassadorRepository.findById(ambassadorRequest.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ambassadeur non trouvé"));

        Map<String, AmbassadorTranslation> translationsByLang = existingAmbassador.getTranslations().stream()
                        .collect(Collectors.toMap(AmbassadorTranslation::getLang, Function.identity()));

        ambassadorRequest.translations().forEach((lang, tRequest) -> {
            AmbassadorTranslation translation = translationsByLang.get(lang.toLowerCase());
            if (translation != null) {
                translation.setDescription(tRequest.description());
            }
        });

        existingAmbassador.setName(ambassadorRequest.name());
        existingAmbassador.setInstagramUsername(ambassadorRequest.instagramUsername());
        existingAmbassador.setTiktokUsername(ambassadorRequest.tiktokUsername());
        existingAmbassador.setTwitchUsername(ambassadorRequest.twitchUsername());
        existingAmbassador.setYoutubeUsername(ambassadorRequest.youtubeUsername());
        existingAmbassador.setTwitterXUsername(ambassadorRequest.twitterXUsername());

        try {
            if(imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, existingAmbassador.getName().toLowerCase(), Bucket.AMBASSADORS);
                existingAmbassador.setImageKey(imageUrl);
            }
        } catch (Exception e) {
            log.error("Error uploading image for ambassador: {}", existingAmbassador.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de l'upload de l'image de l'ambassadeur", e);
        }
        return ambassadorMapper.toAmbassadorDto(ambassadorRepository.save(existingAmbassador));

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
