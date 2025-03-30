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
                .build();
    }

    private int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
