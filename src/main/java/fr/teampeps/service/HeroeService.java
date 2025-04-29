package fr.teampeps.service;

import fr.teampeps.dto.HeroeDto;
import fr.teampeps.mapper.HeroeMapper;
import fr.teampeps.enums.Bucket;
import fr.teampeps.enums.Game;
import fr.teampeps.models.Heroe;
import fr.teampeps.repository.HeroeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeroeService {

    private final HeroeRepository heroeRepository;
    private final HeroeMapper heroeMapper;
    private final MinioService minioService;

    private static final String SLASH_DELIMITER = "/";

    public Map<String, List<HeroeDto>> getAllHeroes() {

        List<HeroeDto> heroesOverwatch = heroeRepository.findAllByGameOrderByNameAsc(Game.OVERWATCH).stream()
                .map(heroeMapper::toHeroeDto)
                .toList();

        List<HeroeDto> heroesMarvelRivals = heroeRepository.findAllByGameOrderByNameAsc(Game.MARVEL_RIVALS)
                .stream()
                .map(heroeMapper::toHeroeDto)
                .toList();

        return Map.of(
            "overwatch", heroesOverwatch,
            "marvel-rivals", heroesMarvelRivals
        );
    }

    public HeroeDto saveHeroe(Heroe heroe, MultipartFile imageFile) {

        if(imageFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucune image fournie");
        }

        try {
            String fileName = heroe.getGame() + SLASH_DELIMITER + heroe.getName().toLowerCase();
            String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, fileName, Bucket.HEROES);
            heroe.setImageKey(imageUrl);

            return heroeMapper.toHeroeDto(heroeRepository.save(heroe));

        } catch (Exception e) {
            log.error("Error saving heroe with ID: {}", heroe.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du héro", e);
        }
    }

    public HeroeDto updateHeroe(Heroe heroe, MultipartFile imageFile) {

        try {
            if(imageFile != null) {
                String fileName = heroe.getGame() + SLASH_DELIMITER + heroe.getName().toLowerCase();
                String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, fileName, Bucket.HEROES);
                heroe.setImageKey(imageUrl);
            }

            return heroeMapper.toHeroeDto(heroeRepository.save(heroe));

        } catch (Exception e) {
            log.error("Error saving heroe with ID: {}", heroe.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du héro", e);
        }
    }
}
