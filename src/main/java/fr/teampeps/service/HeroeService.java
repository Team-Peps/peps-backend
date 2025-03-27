package fr.teampeps.service;

import fr.teampeps.dto.HeroeDto;
import fr.teampeps.mapper.HeroeMapper;
import fr.teampeps.repository.HeroeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HeroeService {

    private final HeroeRepository heroeRepository;
    private final HeroeMapper heroeMapper;

    public Set<HeroeDto> getAllHeroesByGame(String game) {
        return heroeRepository.findAllByGameOrderByNameAsc(game).stream()
                .map(heroeMapper::toHeroeDto)
                .collect(Collectors.toSet());
    }
}
