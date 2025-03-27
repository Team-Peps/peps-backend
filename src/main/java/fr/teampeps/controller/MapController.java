package fr.teampeps.controller;

import fr.teampeps.dto.MapDto;
import fr.teampeps.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @GetMapping("{game}")
    public Set<MapDto> getAllMapsByGame(@PathVariable String game) {
        return mapService.getAllMapsByGame(game);
    }

}
