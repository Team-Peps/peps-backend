package fr.teampeps.controller;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.dto.MemberTinyDto;
import fr.teampeps.model.Game;
import fr.teampeps.model.member.Member;
import fr.teampeps.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/game/{game}")
    public ResponseEntity<Map<String, List<MemberDto>>> getAllMembers(@PathVariable Game game) {
        return ResponseEntity.ok(memberService.getAllMembers(game));
    }

    @GetMapping("/game/{game}/active")
    public ResponseEntity<Map<String, List<MemberTinyDto>>> getAllActiveMembers(@PathVariable Game game) {
        return ResponseEntity.ok(memberService.getAllActiveMembersByGame(game));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> getMemberDetails(@PathVariable String id) {
        MemberDto member = memberService.getMemberDetails(id);
        return ResponseEntity.ok(member);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateMember(
            @RequestPart("member") Member member,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            MemberDto updatedMember = memberService.saveOrUpdateMember(member, imageFile);
            return ResponseEntity.ok(Map.of(
                    "message", "Membre mis à jour avec succès",
                    "member", updatedMember
            ));
        } catch (Exception e) {
            log.error("❌ Error processing member with ID: {}", member.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors du traitement du membre",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> saveMember(
            @RequestPart("member") Member member,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        try {
            MemberDto updatedMember = memberService.saveOrUpdateMember(member, imageFile);
            return ResponseEntity.ok(Map.of(
                    "message", "Membre enregistré avec succès",
                    "member", updatedMember
            ));
        } catch (Exception e) {
            log.error("❌ Error processing member with ID: {}", member.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors du traitement du membre",
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteMember(@PathVariable String id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.ok(Map.of("message", "Membre supprimé avec succès"));
        } catch (DataAccessException e) {
            log.error("❌ Error deleting member with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors de la suppression du membre",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/active")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleActive(@PathVariable String id) {
        try {
            MemberDto updatedMember = memberService.toggleActive(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Membre activé avec succès",
                    "member", updatedMember
            ));
        } catch (DataAccessException e) {
            log.error("❌ Error activating member with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors de l'activation du membre",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/substitute")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleSubstitute(@PathVariable String id) {
        try {
            MemberDto updatedMember = memberService.toggleSubstitute(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Membre changé en remplacant avec succès",
                    "member", updatedMember
            ));
        } catch (DataAccessException e) {
            log.error("❌ Error activating member with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors de l'activation du membre",
                    "error", e.getMessage()
            ));
        }
    }

}
