package fr.teampeps.controller;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.dto.MemberShortDto;
import fr.teampeps.dto.OpponentMemberDto;
import fr.teampeps.dto.PepsMemberDto;
import fr.teampeps.model.member.Member;
import fr.teampeps.model.member.OpponentMember;
import fr.teampeps.model.member.PepsMember;
import fr.teampeps.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @DeleteMapping("/{id}/roster")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> removeMemberFromRoster(@PathVariable String id) {
        try {
            boolean removed = memberService.removeMemberFromRoster(id);

            if (!removed) {
                log.warn("⚠️ Tentative de suppression d'un membre du roster, mais non trouvé : {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Membre introuvable ou déjà retiré du roster", "memberId", id));
            }

            log.info("✅ Membre retiré du roster avec succès : {}", id);
            return ResponseEntity.ok(Map.of("message", "Membre retiré du roster avec succès", "memberId", id));

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Tentative avec un ID invalide : {}", id, e);
            return ResponseEntity.badRequest().body(Map.of("error", "ID invalide", "memberId", id));

        } catch (DataAccessException e) {
            log.error("❌ Erreur de base de données lors de la suppression du membre {} du roster", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne lors de la suppression du membre du roster"));

        } catch (Exception e) {
            log.error("❌ Erreur inattendue lors de la suppression du membre {} du roster", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur inconnue", "memberId", id));
        }
    }

    @PostMapping("/{id}/roster/{rosterId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> addMemberToRoster(@PathVariable String id, @PathVariable String rosterId) {
        try {
            boolean added = memberService.addMemberToRoster(id, rosterId);

            if (!added) {
                log.warn("⚠️ Tentative d'ajout d'un membre au roster, mais non trouvé : {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Membre introuvable ou déjà ajouté au roster", "memberId", id));
            }

            log.info("✅ Membre ajouté au roster avec succès : {}", id);
            return ResponseEntity.ok(Map.of("message", "Membre ajouté au roster avec succès", "memberId", id));

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Tentative avec un ID invalide : {}", id, e);
            return ResponseEntity.badRequest().body(Map.of("error", "ID invalide", "memberId", id));

        } catch (DataAccessException e) {
            log.error("❌ Erreur de base de données lors de l'ajout du membre {} au roster", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne lors de l'ajout du membre au roster"));

        } catch (Exception e) {
            log.error("❌ Erreur inattendue lors de l'ajout du membre {} au roster", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur inconnue", "memberId", id));
        }
    }

    @GetMapping("/without-roster")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Set<MemberShortDto>> getAllMembersWithoutRoster() {
        return ResponseEntity.ok(memberService.getAllMembersWithoutRoster());
    }

    @GetMapping("/peps")
    public ResponseEntity<Set<PepsMemberDto>> getAllPepsMembers() {
        return ResponseEntity.ok(memberService.getAllPepsMembers());
    }

    @PutMapping("/peps")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePepsMember(
            @RequestPart("member") PepsMember member,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            MemberDto updatedMember = memberService.updatePepsMember(member, imageFile);
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

    @PostMapping("/peps")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> savePepsMember(
            @RequestPart("member") PepsMember member,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        try {
            MemberDto updatedMember = memberService.savePepsMember(member, imageFile);
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

    @GetMapping("/opponent")
    public ResponseEntity<Set<OpponentMemberDto>> getAllOpponentMembers() {
        return ResponseEntity.ok(memberService.getAllOpponentMembers());
    }

/*
    @PutMapping("/opponent")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateOpponentMember(@RequestBody OpponentMember member) {
        return handleMemberOperation(member, "Updating opponent member", "Membre mis à jour avec succès", memberService::updateMember);
    }*/

    @PostMapping("/opponent")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> saveOpponentMember(@RequestBody OpponentMember member) {
        return handleMemberOperation(member, "Saving opponent member", "Membre enregistré avec succès", memberService::saveMember);
    }

    @GetMapping("/roster/{rosterId}")
    public ResponseEntity<Set<MemberShortDto>> getMembersByRosterId(@PathVariable String rosterId) {
        return ResponseEntity.ok(memberService.getMemberByRosterId(rosterId));
    }

    /**
     * Handle member operation.
     * @param member
     * @param logMessage
     * @param successMessage
     * @param memberFunction
     * @return
     * @param <T>
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
