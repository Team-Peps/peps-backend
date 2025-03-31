package fr.teampeps.service;

import fr.teampeps.dto.HeroeDto;
import fr.teampeps.mapper.HeroeMapper;
import fr.teampeps.model.Bucket;
import fr.teampeps.model.Game;
import fr.teampeps.model.heroe.Heroe;
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

    public HeroeDto saveOrUpdateHeroe(Heroe heroe, MultipartFile imageFile) {
        log.info("Updating heroe : {}", heroe);

        try {
            if (imageFile != null) {
                String fileName = heroe.getGame() + "/" + heroe.getName().toLowerCase();
                log.info("Saving heroe : {}", fileName);
                String imageUrl = minioService.uploadImage(imageFile, fileName, Bucket.HEROES);
                heroe.setImageKey(imageUrl);
            }

            return heroeMapper.toHeroeDto(heroeRepository.save(heroe));

        } catch (Exception e) {
            log.error("Error saving heroe with ID: {}", heroe.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du héro", e);
        }
    }
}
