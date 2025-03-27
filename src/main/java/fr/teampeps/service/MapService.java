package fr.teampeps.service;

import fr.teampeps.dto.MapDto;
import fr.teampeps.mapper.MapMapper;
import fr.teampeps.model.map.Map;
import fr.teampeps.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapService {

    private final MapRepository mapRepository;
    private final MapMapper mapMapper;

    public Set<MapDto> getAllMapsByGame(String game) {
        return mapRepository.findAllByGameOrderByNameAsc(game).stream()
                .map(mapMapper::toMapDto)
                .collect(Collectors.toSet());
    }

}
