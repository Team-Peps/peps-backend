package fr.teampeps.controller;

import fr.teampeps.model.match.Match;
import fr.teampeps.service.CronService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {
    private final CronService cronService;

    @GetMapping
    public Map<String, List<Match>> test() {
        return cronService.fetchAndSaveMatches();
    }
}
