package fr.teampeps.controller;

import fr.teampeps.dto.MatchDto;
import fr.teampeps.dto.MatchGroupByDate;
import fr.teampeps.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/match")
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

    @GetMapping("/upcoming")
    public ResponseEntity<List<MatchGroupByDate>> getUpcomingMatches() {
        List<MatchGroupByDate> upcomingMatches = matchService.getNext5Matches();
        return ResponseEntity.ok(upcomingMatches);
    }

}
