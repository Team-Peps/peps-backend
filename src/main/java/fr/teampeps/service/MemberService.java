package fr.teampeps.service;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.dto.MemberTinyDto;
import fr.teampeps.mapper.MemberMapper;
import fr.teampeps.model.Bucket;
import fr.teampeps.model.Game;
import fr.teampeps.model.heroe.Heroe;
import fr.teampeps.model.member.Member;
import fr.teampeps.repository.HeroeRepository;
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
    private final HeroeRepository heroeRepository;

    private static final String MEMBER_NOT_FOUND = "Membre non trouvé";

    public MemberDto saveOrUpdateMember(Member member, MultipartFile imageFile) {

        if(imageFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucune image fournie");
        }

        if(member.getFavoriteHeroes() != null && member.getFavoriteHeroes().size() > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un membre ne peut avoir plus de 3 héros favoris");
        }

        if (member.getFavoriteHeroes() != null) {
            List<Heroe> validatedHeroes = member.getFavoriteHeroes().stream()
                    .map(hero -> heroeRepository.findById(hero.getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Héros introuvable : " + hero.getId())))
                    .toList();

            member.setFavoriteHeroes(validatedHeroes);
        }

        try {
            String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, member.getPseudo().toLowerCase(), Bucket.MEMBERS);
            member.setImageKey(imageUrl);

            return memberMapper.toMemberDto(memberRepository.save(member));

        } catch (Exception e) {
            log.error("Error saving member with ID: {}", member.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du membre", e);
        }
    }

    public Map<String, List<MemberDto>> getAllMembers(Game game) {

        List<MemberDto> members = memberRepository.findAllActiveHolderByGame(game).stream()
                .map(memberMapper::toMemberDto)
                .toList();

        List<MemberDto> substitutes = memberRepository.findAllActiveSubstituteByGame(game).stream()
                .map(memberMapper::toMemberDto)
                .toList();

        List<MemberDto> coaches = memberRepository.findAllActiveCoachByGame(game).stream()
                .map(memberMapper::toMemberDto)
                .toList();

        List<MemberDto> inactives = memberRepository.findAllInactiveByGame(game).stream()
                .map(memberMapper::toMemberDto)
                .toList();

        return Map.of(
                "members", members,
                "substitutes", substitutes,
                "coaches", coaches,
                "inactives", inactives
        );
    }

    public Map<String, List<MemberTinyDto>> getAllActiveMembersByGame(Game game) {

        List<MemberTinyDto> members = memberRepository.findAllActiveHolderByGame(game).stream()
                .map(memberMapper::toMemberTinyDto)
                .toList();

        List<MemberTinyDto> substitutes = memberRepository.findAllActiveSubstituteByGame(game).stream()
                .map(memberMapper::toMemberTinyDto)
                .toList();

        List<MemberTinyDto> coaches = memberRepository.findAllActiveCoachByGame(game).stream()
                .map(memberMapper::toMemberTinyDto)
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND, e);
        }
    }

    public MemberDto toggleActive(String id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND));

        member.setIsActive(!member.getIsActive());

        return memberMapper.toMemberDto(memberRepository.save(member));
    }

    public MemberDto toggleSubstitute(String id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND));

        member.setIsSubstitute(!member.getIsSubstitute());

        return memberMapper.toMemberDto(memberRepository.save(member));
    }

    public MemberDto getMemberDetails(String id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND));

        return memberMapper.toMemberDto(member);
    }
}
