package fr.teampeps.mapper;

import fr.teampeps.dto.*;
import fr.teampeps.model.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final AchievementMapper achievementMapper;
    private final HeroeMapper heroeMapper;

    public MemberDto toMemberDto(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .pseudo(member.getPseudo())
                .firstname(member.getFirstname())
                .lastname(member.getLastname())
                .description(member.getDescription())
                .nationality(member.getNationality())
                .age(calculateAge(member.getDateOfBirth()))
                .dateOfBirth(member.getDateOfBirth().toString())
                .role(member.getRole())
                .isSubstitute(member.getIsSubstitute())
                .imageKey(member.getImageKey())
                .xUsername(member.getXUsername())
                .instagramUsername(member.getInstagramUsername())
                .tiktokUsername(member.getTiktokUsername())
                .youtubeUsername(member.getYoutubeUsername())
                .twitchUsername(member.getTwitchUsername())
                .game(member.getGame())
                .achievements(achievementMapper.toAchievementDtoList(member.getAchievements()))
                .favoriteHeroes(heroeMapper.toHeroeDtoList(member.getFavoriteHeroes()))
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

    private int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
