package fr.teampeps.controller;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.dto.MemberTinyDto;
import fr.teampeps.enums.Game;
import fr.teampeps.models.Member;
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

@RestController
@RequestMapping("/v1/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private static final String MESSAGE_PLACEHOLDER = "message";
    private static final String ERROR_PLACEHOLDER = "error";
    private static final String MEMBER_PLACEHOLDER = "member";

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
            MemberDto updatedMember = memberService.updateMember(member, imageFile);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Membre mis à jour avec succès",
                    MEMBER_PLACEHOLDER, updatedMember
            ));
        } catch (Exception e) {
            log.error("❌ Error processing member with ID: {}", member.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors du traitement du membre",
                    ERROR_PLACEHOLDER, e.getMessage()
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
            MemberDto updatedMember = memberService.saveMember(member, imageFile);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Membre enregistré avec succès",
                    MEMBER_PLACEHOLDER, updatedMember
            ));
        } catch (Exception e) {
            log.error("❌ Error processing member with ID: {}", member.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors du traitement du membre",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteMember(@PathVariable String id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.ok(Map.of(MESSAGE_PLACEHOLDER, "Membre supprimé avec succès"));
        } catch (DataAccessException e) {
            log.error("❌ Error deleting member with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de la suppression du membre",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/active")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleActive(@PathVariable String id) {
        try {
            MemberDto updatedMember = memberService.toggleActive(id);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Membre activé avec succès",
                    MEMBER_PLACEHOLDER, updatedMember
            ));
        } catch (DataAccessException e) {
            log.error("❌ Error activating member with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de l'activation du membre",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/substitute")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleSubstitute(@PathVariable String id) {
        try {
            MemberDto updatedMember = memberService.toggleSubstitute(id);
            return ResponseEntity.ok(Map.of(
                    MESSAGE_PLACEHOLDER, "Membre changé en remplacant avec succès",
                    MEMBER_PLACEHOLDER, updatedMember
            ));
        } catch (DataAccessException e) {
            log.error("❌ Error activating member with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    MESSAGE_PLACEHOLDER, "Erreur lors de l'activation du membre",
                    ERROR_PLACEHOLDER, e.getMessage()
            ));
        }
    }

}
