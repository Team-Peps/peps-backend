package fr.teampeps.service;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.mapper.MemberMapper;
import fr.teampeps.model.Member;
import fr.teampeps.model.Roster;
import fr.teampeps.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Transactional
    public MemberDto updateMember(Member member) {
         log.info("Updating member : {}", member);
        try {
            Roster roster = memberRepository.findById(member.getId())
                   .map(Member::getRoster)
                   .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membre non trouvé avec l'ID: " + member.getId()));
            member.setRoster(roster);
            return memberMapper.map(memberRepository.save(member));
        } catch (Exception e) {
            log.error("Error saving member with ID: {}", member.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du membre", e);
        }
    }

    public Set<MemberDto> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(memberMapper::map)
                .collect(Collectors.toSet());
    }

    public MemberDto saveMember(Member member) {
        log.info("Saving member : {}", member);
        try {
            return memberMapper.map(memberRepository.save(member));
        } catch (Exception e) {
            log.error("Error saving member", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de l'enregistrement du membre", e);
        }
    }
}
