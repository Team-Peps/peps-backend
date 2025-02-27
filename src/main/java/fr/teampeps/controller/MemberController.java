package fr.teampeps.controller;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.model.Member;
import fr.teampeps.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<Set<MemberDto>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateMember(@RequestBody Member member) {
        log.info("Updating member with ID: {}", member.getId());
        try {
            MemberDto updatedMember = memberService.updateMember(member);
            return ResponseEntity.ok(Map.of(
                    "message", "Membre mis à jour avec succès",
                    "member", updatedMember
            ));
        } catch (Exception e) {
            log.error("Error updating member with ID: {}", member.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Erreur lors de la mise à jour du membre",
                    "error", e.getMessage()
            ));
        }
    }


}
