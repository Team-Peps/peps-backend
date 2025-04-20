package fr.teampeps.service;

import fr.teampeps.dto.HeroeDto;
import fr.teampeps.mapper.HeroeMapper;
import fr.teampeps.enums.Bucket;
import fr.teampeps.enums.Game;
import fr.teampeps.models.Heroe;
import fr.teampeps.repository.HeroeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeroeServiceTest {

    @Mock
    private HeroeRepository heroeRepository;

    @Mock
    private HeroeMapper heroeMapper;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private HeroeService heroeService;

    @Test
    void getAllHeroes_shouldReturnMapOfHeroesGroupedByGame() {
        Heroe overwatchHero = new Heroe();
        HeroeDto overwatchHeroDto = HeroeDto.builder().build();
        Heroe marvelHero = new Heroe();
        HeroeDto marvelHeroDto = HeroeDto.builder().build();

        when(heroeRepository.findAllByGameOrderByNameAsc(Game.OVERWATCH)).thenReturn(List.of(overwatchHero));
        when(heroeRepository.findAllByGameOrderByNameAsc(Game.MARVEL_RIVALS)).thenReturn(List.of(marvelHero));
        when(heroeMapper.toHeroeDto(overwatchHero)).thenReturn(overwatchHeroDto);
        when(heroeMapper.toHeroeDto(marvelHero)).thenReturn(marvelHeroDto);

        Map<String, List<HeroeDto>> result = heroeService.getAllHeroes();

        assertEquals(1, result.get("overwatch").size());
        assertEquals(1, result.get("marvel-rivals").size());
        assertEquals(overwatchHeroDto, result.get("overwatch").get(0));
        assertEquals(marvelHeroDto, result.get("marvel-rivals").get(0));
    }

    @Test
    void saveOrUpdateHeroe_shouldSaveHeroeWithImage() {
        Heroe heroe = new Heroe();
        heroe.setGame(Game.OVERWATCH);
        heroe.setName("Tracer");
        MultipartFile imageFile = mock(MultipartFile.class);

        String imageUrl = "heroes/overwatch/tracer.png";
        when(minioService.uploadImageFromMultipartFile(any(), eq("OVERWATCH/tracer"), eq(Bucket.HEROES)))
                .thenReturn(imageUrl);

        Heroe savedHeroe = new Heroe();
        savedHeroe.setImageKey(imageUrl);
        HeroeDto heroDto = HeroeDto.builder().build();

        when(heroeRepository.save(any())).thenReturn(savedHeroe);
        when(heroeMapper.toHeroeDto(savedHeroe)).thenReturn(heroDto);

        HeroeDto result = heroeService.saveOrUpdateHeroe(heroe, imageFile);

        assertEquals(heroDto, result);
        assertEquals(imageUrl, heroe.getImageKey());
    }

    @Test
    void saveOrUpdateHeroe_shouldSaveHeroeWithoutImage() {
        Heroe heroe = new Heroe();
        heroe.setGame(Game.OVERWATCH);
        heroe.setName("Tracer");
        MultipartFile imageFile = null;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> heroeService.saveOrUpdateHeroe(heroe, imageFile));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Aucune image fournie", exception.getReason());
    }

    @Test
    void saveOrUpdateHeroe_shouldThrowException_whenErrorOccurs() {
        Heroe heroe = new Heroe();
        heroe.setName("Tracer");
        MultipartFile file = mock(MultipartFile.class);

        when(minioService.uploadImageFromMultipartFile(any(), any(), any())).thenThrow(new RuntimeException("Upload failed"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> heroeService.saveOrUpdateHeroe(heroe, file));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Erreur lors de la mise à jour du héro"));
    }
}
