package fr.teampeps.mapper;

import fr.teampeps.dto.MapDto;
import fr.teampeps.model.map.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MapMapper {

    public MapDto toMapDto(Map map) {
        return MapDto.builder()
                .id(map.getId())
                .name(map.getName())
                .type(map.getType().name())
                .imageKey(map.getImageKey())
                .build();
    }
}
