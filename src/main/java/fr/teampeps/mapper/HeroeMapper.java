package fr.teampeps.mapper;

import fr.teampeps.dto.HeroeDto;
import fr.teampeps.model.Heroe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeroeMapper {

    public HeroeDto toHeroeDto(Heroe heroe) {
        return HeroeDto.builder()
                .id(heroe.getId())
                .name(heroe.getName())
                .role(heroe.getRole())
                .imageKey(heroe.getImageKey())
                .build();
    }
}
