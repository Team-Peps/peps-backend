package fr.teampeps.controller;

import fr.teampeps.dto.RosterDto;
import fr.teampeps.dto.RosterMediumDto;
import fr.teampeps.dto.RosterShortDto;
import fr.teampeps.model.Roster;
import fr.teampeps.service.RosterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<RosterMediumDto> getRoster(@PathVariable String id) {
        return ResponseEntity.ok(rosterService.getRoster(id));
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RosterShortDto> creatRoster(
            @RequestPart("roster") Roster roster,
            @RequestPart(value = "imageFile") MultipartFile imageFile
    ) {
        RosterShortDto createdRoster = rosterService.createRoster(roster, imageFile);
        return ResponseEntity.status(201).body(createdRoster);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteRoster(@PathVariable String id) {
        rosterService.deleteRoster(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RosterShortDto> updateRoster(
            @RequestPart("roster") Roster roster,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        RosterShortDto updatedRoster = rosterService.updateRoster(roster, imageFile);
        return ResponseEntity.ok(updatedRoster);
    }

}
