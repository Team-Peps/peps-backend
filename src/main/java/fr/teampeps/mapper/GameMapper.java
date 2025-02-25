package fr.teampeps.mapper;

import fr.teampeps.dto.GameDto;
import fr.teampeps.model.Game;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public GameDto map(Game game){
        return GameDto.builder()
                .id(game.getId())
                .name(game.getName())
                .build();
    }

}
