package fr.teampeps.mapper;

import fr.teampeps.dto.*;
import fr.teampeps.models.Member;
import fr.teampeps.models.MemberTranslation;
import fr.teampeps.record.MemberRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final AchievementMapper achievementMapper;
    private final HeroeMapper heroeMapper;

    public MemberDto toMemberDto(Member member){
        Map<String, MemberTranslationDto> translationsDto = member.getTranslations().stream()
                .collect(Collectors.toMap(
                        MemberTranslation::getLang,
                        t -> MemberTranslationDto.builder()
                                .description(t.getDescription())
                                .build()
                ));

        return MemberDto.builder()
                .id(member.getId())
                .pseudo(member.getPseudo())
                .firstname(member.getFirstname())
                .lastname(member.getLastname())
                .nationality(member.getNationality())
                .age(calculateAge(member.getDateOfBirth()))
                .dateOfBirth(member.getDateOfBirth().toString())
                .role(member.getRole())
                .isSubstitute(member.getIsSubstitute())
                .isActive(member.getIsActive())
                .imageKey(member.getImageKey())
                .twitterUsername(member.getTwitterUsername())
                .instagramUsername(member.getInstagramUsername())
                .tiktokUsername(member.getTiktokUsername())
                .youtubeUsername(member.getYoutubeUsername())
                .twitchUsername(member.getTwitchUsername())
                .game(member.getGame())
                .achievements(achievementMapper.toAchievementDtoList(member.getAchievements()))
                .favoriteHeroes(heroeMapper.toHeroeDtoList(member.getFavoriteHeroes()))
                .translations(translationsDto)
                .build();
    }

    public MemberTinyDto toMemberTinyDto(Member member){
        return MemberTinyDto.builder()
                .id(member.getId())
                .pseudo(member.getPseudo())
                .role(member.getRole())
                .isSubstitute(member.getIsSubstitute())
                .imageKey(member.getImageKey())
                .build();
    }

    public Member toMember(MemberRequest memberRequest) {
        return Member.builder()
                .id(memberRequest.id() != null ? memberRequest.id() : null)
                .pseudo(memberRequest.pseudo())
                .firstname(memberRequest.firstname())
                .lastname(memberRequest.lastname())
                .game(memberRequest.game())
                .isActive(memberRequest.isActive())
                .isSubstitute(memberRequest.isSubstitute())
                .favoriteHeroes(memberRequest.favoriteHeroes())
                .dateOfBirth(memberRequest.dateOfBirth())
                .nationality(memberRequest.nationality())
                .role(memberRequest.role())
                .twitterUsername(memberRequest.twitterUsername())
                .instagramUsername(memberRequest.instagramUsername())
                .tiktokUsername(memberRequest.tiktokUsername())
                .youtubeUsername(memberRequest.youtubeUsername())
                .twitchUsername(memberRequest.twitchUsername())
                .game(memberRequest.game())
                .translations(memberRequest.translations().entrySet().stream()
                        .map(entry -> {
                            MemberTranslation translation = new MemberTranslation();
                            translation.setLang(entry.getKey());
                            translation.setDescription(entry.getValue().description());
                            return translation;
                        }).collect(Collectors.toList()))
                .build();
    }

    private int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
