package fr.teampeps.controller;

import fr.teampeps.dto.MatchDto;
import fr.teampeps.dto.MatchGroupByDate;
import fr.teampeps.model.Game;
import fr.teampeps.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<Map<String, List<MatchDto>>> getAllMatches() {
        Map<String, List<MatchDto>> matches = matchService.getAllMatchesSortedByDateGroupByGame();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/current")
    public ResponseEntity<Optional<MatchDto>> getCurrentMatch() {
        Optional<MatchDto> currentMatch = matchService.getCurrentMatch();
        if(currentMatch.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(currentMatch);
    }

    @GetMapping("/upcoming/5")
    public ResponseEntity<List<MatchGroupByDate>> getNext5Matches() {
        List<MatchGroupByDate> next5Matches = matchService.getNext5Matches();
        return ResponseEntity.ok(next5Matches);
    }

    @GetMapping("/upcoming/game/{game}")
    public ResponseEntity<List<MatchGroupByDate>> getUpcomingMatchesByGame(@PathVariable Game game) {
        List<MatchGroupByDate> upcomingMatchesByGame = matchService.getUpcomingMatchesByGame(game);
        return ResponseEntity.ok(upcomingMatchesByGame);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Page<MatchGroupByDate>> getUpcomingMatches(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "filter", defaultValue = "OVERWATCH, MARVEL_RIVALS") String filter
    ) {
        Page<MatchGroupByDate> upcomingMatches = matchService.getUpcomingMatches(page, filter);
        return ResponseEntity.ok(upcomingMatches);
    }

    @GetMapping("/result")
    public ResponseEntity<Page<MatchGroupByDate>> getMatchResults(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "filter", defaultValue = "OVERWATCH, MARVEL_RIVALS") String filter
    ) {
        Page<MatchGroupByDate> matchGroups = matchService.getResultsMatches(page, filter);
        return ResponseEntity.ok(matchGroups);
    }

}
