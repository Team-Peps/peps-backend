package fr.teampeps.controller;

import fr.teampeps.model.Roster;
import fr.teampeps.model.match.Match;
import fr.teampeps.model.record.MatchCreateFinishedRequest;
import fr.teampeps.model.record.MatchCreateRequest;
import fr.teampeps.repository.RosterRepository;
import fr.teampeps.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final RosterRepository rosterRepository;

    @PostMapping("/upcoming")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, String>> createUpcomingMatch(@RequestBody MatchCreateRequest request) {
        Optional<Roster> rosterOptional = rosterRepository.findById(request.roster());
        Optional<Roster> opponentOptional = rosterRepository.findById(request.opponentRoster());

        if (rosterOptional.isEmpty() || opponentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Roster ou adversaire introuvable"));
        }

        Match match = Match.builder()
                .competitionName(request.competitionName())
                .date(LocalDateTime.parse(request.date()))
                .type(request.type())
                .roster(rosterOptional.get())
                .opponentRoster(opponentOptional.get())
                .build();

        boolean isMatchCreated = matchService.createUpcomingMatch(match);

        if (isMatchCreated) {
            return ResponseEntity.ok(Map.of("message", "Match créer avec succès"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/finished")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, String>> createFinishedMatch(@RequestBody MatchCreateFinishedRequest request) {

        boolean isMatchCreated = matchService.createFinishedMatch(request);

        if (isMatchCreated) {
            return ResponseEntity.ok(Map.of("message", "Match créer avec succès"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
