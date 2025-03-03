package fr.teampeps.controller;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.dto.OpponentMemberDto;
import fr.teampeps.dto.PepsMemberDto;
import fr.teampeps.model.member.Member;
import fr.teampeps.model.member.OpponentMember;
import fr.teampeps.model.member.PepsMember;
import fr.teampeps.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/peps")
    public ResponseEntity<Set<PepsMemberDto>> getAllPepsMembers() {
        return ResponseEntity.ok(memberService.getAllPepsMembers());
    }

    @GetMapping("/opponent")
    public ResponseEntity<Set<OpponentMemberDto>> getAllOpponentMembers() {
        return ResponseEntity.ok(memberService.getAllOpponentMembers());
    }

    @PutMapping("/peps")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePepsMember(@RequestBody PepsMember member) {
        return handleMemberOperation(member, "Updating peps member", "Membre mis à jour avec succès", memberService::updateMember);
    }

    @PostMapping("/peps")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> savePepsMember(@RequestBody PepsMember member) {
        return handleMemberOperation(member, "Saving peps member", "Membre enregistré avec succès", memberService::saveMember);
    }

    @PutMapping("/opponent")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateOpponentMember(@RequestBody OpponentMember member) {
        return handleMemberOperation(member, "Updating opponent member", "Membre mis à jour avec succès", memberService::updateMember);
    }

    @PostMapping("/opponent")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> saveOpponentMember(@RequestBody OpponentMember member) {
        return handleMemberOperation(member, "Saving opponent member", "Membre enregistré avec succès", memberService::saveMember);
    }

    /**
     * Méthode générique pour gérer les opérations de sauvegarde et mise à jour des membres.
     */
    private <T extends Member> ResponseEntity<Map<String, Object>> handleMemberOperation(
            T member, String logMessage, String successMessage, Function<T, MemberDto> memberFunction) {

        log.info("{} with ID: {}", logMessage, member.getId());
        try {
            MemberDto updatedMember = memberFunction.apply(member);
            return ResponseEntity.ok(Map.of(
                    "message", successMessage,
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
}
