package fr.teampeps.controller;

import fr.teampeps.dto.RosterDto;
import fr.teampeps.dto.RosterShortDto;
import fr.teampeps.exceptions.DatabaseException;
import fr.teampeps.model.Roster;
import fr.teampeps.service.RosterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/roster")
@RequiredArgsConstructor
@Slf4j
public class RosterController {

    private final RosterService rosterService;

    @GetMapping("/peps")
    public ResponseEntity<Set<RosterShortDto>> getAllPepsRosters() {
        return ResponseEntity.ok(rosterService.getAllPepsRosters());
    }

    @GetMapping("/opponent")
    public ResponseEntity<Set<RosterShortDto>> getAllOpponentRosters() {
        return ResponseEntity.ok(rosterService.getAllOpponentRosters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RosterDto> getRoster(@PathVariable String id) {
        return ResponseEntity.ok(rosterService.getRoster(id));
    }

    @PostMapping("/opponent")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RosterShortDto> createOpponentRoster(@RequestBody Roster roster) {
        RosterShortDto createdRoster = rosterService.createOpponentRoster(roster);
        return ResponseEntity.status(201).body(createdRoster);
    }

    @DeleteMapping("/opponent/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteOpponentRoster(@PathVariable String id) {
        rosterService.deleteOpponentRoster(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/opponent/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RosterShortDto> updateOpponentRoster(@PathVariable String id, @RequestBody Roster roster) {
        RosterShortDto updatedRoster = rosterService.updateOpponentRoster(id, roster);
        return ResponseEntity.ok(updatedRoster);
    }

}
