package fr.teampeps.service;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.dto.MemberTinyDto;
import fr.teampeps.mapper.MemberMapper;
import fr.teampeps.enums.Bucket;
import fr.teampeps.enums.Game;
import fr.teampeps.models.Achievement;
import fr.teampeps.models.Heroe;
import fr.teampeps.models.Member;
import fr.teampeps.models.MemberTranslation;
import fr.teampeps.record.MemberRequest;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final MinioService minioService;
    private final HeroeRepository heroeRepository;

    private static final String MEMBER_NOT_FOUND = "Membre non trouvé";

    public MemberDto saveMember(
            MemberRequest memberRequest,
            MultipartFile imageFile
    ) {

        if(imageFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucune image fournie");
        }

        Member member = memberMapper.toMember(memberRequest);

        checkIfMemberAsMaxHeroes(member.getFavoriteHeroes());

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

            List<MemberTranslation> validTranslations = member.getTranslations().stream()
                    .filter(t -> t.getLang() != null && !t.getLang().isBlank() && t.getDescription() != null && !t.getDescription().isBlank())
                    .peek(memberTranslation -> memberTranslation.setParent(member))
                    .toList();
            member.setTranslations(validTranslations);
            member.setIsActive(true);

            member.getAchievements().removeIf(achievement ->
                    achievement.getRanking() == null || achievement.getCompetitionName().isEmpty() || achievement.getYear() == null
            );
            member.getAchievements().forEach(achievement -> achievement.setMember(member));

            return memberMapper.toMemberDto(memberRepository.save(member));

        } catch (Exception e) {
            log.error("Error saving member with ID: {}", member.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du membre", e);
        }
    }

    public MemberDto updateMember(
            MemberRequest memberRequest,
            MultipartFile imageFile
    ) {
        Member existingMember = memberRepository.findById(memberRequest.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND));

        checkIfMemberAsMaxHeroes(memberRequest.favoriteHeroes());

        if (memberRequest.favoriteHeroes() != null) {
            List<Heroe> validatedHeroes = memberRequest.favoriteHeroes().stream()
                    .map(hero -> heroeRepository.findById(hero.getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Héros introuvable : " + hero.getId())))
                    .toList();

            existingMember.getFavoriteHeroes().clear();
            existingMember.getFavoriteHeroes().addAll(validatedHeroes);
        }

        List<Achievement> achievements = memberRequest.achievements().stream()
            .filter(
                    achievement -> achievement.getCompetitionName() != null && !achievement.getCompetitionName().isEmpty() &&
                            achievement.getRanking() != null && achievement.getYear() != null
            )
                .map(achievement -> {
                    Achievement memberAchievement = new Achievement();
                    memberAchievement.setCompetitionName(achievement.getCompetitionName());
                    memberAchievement.setRanking(achievement.getRanking());
                    memberAchievement.setMember(existingMember);
                    memberAchievement.setYear(achievement.getYear());
                    return memberAchievement;
                })
                .toList();

        Map<String, MemberTranslation> translationsByLang = existingMember.getTranslations().stream()
                .collect(Collectors.toMap(MemberTranslation::getLang, Function.identity()));

        memberRequest.translations().forEach((lang, tRequest) -> {
            MemberTranslation translation = translationsByLang.get(lang);
            if(translation != null) {
                translation.setDescription(tRequest.description());
            }
        });

        existingMember.setGame(memberRequest.game());
        existingMember.setPseudo(memberRequest.pseudo());
        existingMember.setIsSubstitute(memberRequest.isSubstitute());
        existingMember.setTwitterUsername(memberRequest.twitterUsername());
        existingMember.setTwitchUsername(memberRequest.twitchUsername());
        existingMember.setYoutubeUsername(memberRequest.youtubeUsername());
        existingMember.setInstagramUsername(memberRequest.instagramUsername());
        existingMember.setTiktokUsername(memberRequest.tiktokUsername());
        existingMember.setFirstname(memberRequest.firstname());
        existingMember.setLastname(memberRequest.lastname());
        existingMember.setNationality(memberRequest.nationality());
        existingMember.setRole(memberRequest.role());
        existingMember.setDateOfBirth(memberRequest.dateOfBirth());
        existingMember.getAchievements().clear();
        existingMember.getAchievements().addAll(achievements);

        try {
            if(imageFile != null) {
                String imageUrl = minioService.uploadImageFromMultipartFile(imageFile, memberRequest.pseudo().toLowerCase(), Bucket.MEMBERS);
                existingMember.setImageKey(imageUrl);
            }

            return memberMapper.toMemberDto(memberRepository.save(existingMember));

        } catch (Exception e) {
            log.error("Error saving member with ID: {}", memberRequest.id(), e);
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

    private void checkIfMemberAsMaxHeroes(List<Heroe> favoriteHeroes) {
        if (favoriteHeroes != null && favoriteHeroes.size() > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un membre ne peut avoir plus de 3 héros favoris");
        }
    }
}
