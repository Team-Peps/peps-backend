package fr.teampeps.controller;

import fr.teampeps.dto.HeroeDto;
import fr.teampeps.service.HeroeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/heroe")
@RequiredArgsConstructor
public class HeroeController {

    private final HeroeService heroeService;

    @GetMapping("{game}")
    public Set<HeroeDto> getAllHeroesByGame(@PathVariable String game) {
        return heroeService.getAllHeroesByGame(game);
    }
}
