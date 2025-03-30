package fr.teampeps.service;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.mapper.MemberMapper;
import fr.teampeps.model.Bucket;
import fr.teampeps.model.Game;
import fr.teampeps.model.member.Member;
import fr.teampeps.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final MinioService minioService;

    public MemberDto saveOrUpdateMember(Member member, MultipartFile imageFile) {
        log.info("Updating member : {}", member);

        try {
            if (imageFile != null) {
                String imageUrl = minioService.uploadImage(imageFile, member.getPseudo().toLowerCase(), Bucket.MEMBERS);
                member.setImageKey(imageUrl);
            }

            return memberMapper.toMemberDto(memberRepository.save(member));

        } catch (Exception e) {
            log.error("Error saving member with ID: {}", member.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du membre", e);
        }
    }

    public Map<String, List<MemberDto>> getAllActiveMembers(Game game) {

        List<MemberDto> members = memberRepository.findAllActiveByGame(game).stream()
                .map(memberMapper::toMemberDto)
                .toList();

        List<MemberDto> substitutes = memberRepository.findAllSubstituteByGame(game).stream()
                .map(memberMapper::toMemberDto)
                .toList();

        List<MemberDto> coaches = memberRepository.findAllCoachByGame(game).stream()
                .map(memberMapper::toMemberDto)
                .toList();

        return Map.of(
                "members", members,
                "substitutes", substitutes,
                "coaches", coaches
        );
    }

    public void deleteMember(String id) {
        try {
            memberRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Membre non trouvé", e);
        }
    }

    public MemberDto setActive(String id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membre non trouvé"));

        member.setIsSubstitute(false);

        return memberMapper.toMemberDto(memberRepository.save(member));
    }

    public MemberDto setSubstitute(String id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membre non trouvé"));

        member.setIsSubstitute(true);

        return memberMapper.toMemberDto(memberRepository.save(member));
    }
}
