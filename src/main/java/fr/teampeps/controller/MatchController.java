package fr.teampeps.controller;

import fr.teampeps.dto.MatchDto;
import fr.teampeps.dto.MatchGroupByDate;
import fr.teampeps.enums.Game;
import fr.teampeps.service.CronService;
import fr.teampeps.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("/v1/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final CronService cronService;

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

    @GetMapping(value = "/update-and-save", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter updateAndSaveMatches() {
        SseEmitter sseEmitter = new SseEmitter(0L);
        Executors.newSingleThreadExecutor().submit(() -> cronService.fetchAndSaveMatchesManually(sseEmitter));
        return sseEmitter;
    }

}
