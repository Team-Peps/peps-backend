package fr.teampeps.service;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.dto.OpponentMemberDto;
import fr.teampeps.dto.PepsMemberDto;
import fr.teampeps.mapper.MemberMapper;
import fr.teampeps.model.member.Member;
import fr.teampeps.model.Roster;
import fr.teampeps.model.member.PepsMember;
import fr.teampeps.repository.MemberRepository;
import fr.teampeps.repository.PepsMemberRepository;
import fr.teampeps.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final PepsMemberRepository pepsMemberRepository;

    @Transactional
    public MemberDto updatePepsMember(PepsMember pepsMember, MultipartFile imageFile) {
        log.info("Updating peps member : {}", pepsMember);

        try {

            if(imageFile != null){
                pepsMember.setImage(ImageUtils.compressImage(imageFile.getBytes()));
            }else{
                pepsMember.setImage(pepsMemberRepository.findById(pepsMember.getId())
                        .map(PepsMember::getImage)
                        .orElse(null));
            }

            Roster roster = memberRepository.findById(pepsMember.getId())
                   .map(Member::getRoster)
                   .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membre non trouvé avec l'ID: " + pepsMember.getId()));
            pepsMember.setRoster(roster);

            return memberMapper.map(memberRepository.save(pepsMember));

        } catch (Exception e) {
            log.error("Error saving member with ID: {}", pepsMember.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du membre", e);
        }
    }

    @Transactional
    public MemberDto savePepsMember(PepsMember pepsMember, MultipartFile imageFile) {
        log.info("Saving peps member : {}", pepsMember);
        try {
            pepsMember.setImage(ImageUtils.compressImage(imageFile.getBytes()));
            return memberMapper.map(memberRepository.save(pepsMember));
        } catch (Exception e) {
            log.error("Error saving member", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de l'enregistrement du membre", e);
        }
    }

    @Transactional
    public Set<PepsMemberDto> getAllPepsMembers() {
        return memberRepository.findAllPepsMember().stream()
                .map(memberMapper::toPepsMemberDto)
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

    public Set<OpponentMemberDto> getAllOpponentMembers() {
        return memberRepository.findAllOpponentMember().stream()
                .map(memberMapper::toOpponentMemberDto)
                .collect(Collectors.toSet());
    }
}
