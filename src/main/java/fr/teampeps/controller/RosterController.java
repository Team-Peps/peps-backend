package fr.teampeps.controller;

import fr.teampeps.dto.RosterDto;
import fr.teampeps.dto.RosterShortDto;
import fr.teampeps.service.RosterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/roster")
@RequiredArgsConstructor
public class RosterController {

    private final RosterService rosterService;

    @GetMapping
    public ResponseEntity<Set<RosterShortDto>> getAllRosters() {
        return ResponseEntity.ok(rosterService.getAllRosters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RosterDto> getRoster(@PathVariable String id) {
        return ResponseEntity.ok(rosterService.getRoster(id));
    }

}
