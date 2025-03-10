package fr.teampeps.mapper;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.dto.MemberShortDto;
import fr.teampeps.dto.OpponentMemberDto;
import fr.teampeps.dto.PepsMemberDto;
import fr.teampeps.model.member.Member;
import fr.teampeps.model.member.PepsMember;
import fr.teampeps.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberMapper {

    public MemberDto map(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .role(member.getRole())
                .lastname(member.getLastname())
                .firstname(member.getFirstname())
                .roster(member.getRoster() != null ? member.getRoster().getName() : null)
                .nationality(member.getNationality())
                .pseudo(member.getPseudo())
                .build();
    }

    public PepsMemberDto toPepsMemberDto(PepsMember member) {
        try {
            member.setImage(ImageUtils.decompressImage(member.getImage()));
        } catch (Exception e) {
            log.error("Error decompressing image for member with ID: {}", member.getId(), e);
        }

        return PepsMemberDto.builder()
                .id(member.getId())
                .role(member.getRole())
                .dpi(member.getDpi())
                .dateOfBirth(member.getDateOfBirth().toString())
                .age(member.getDateOfBirth().until(LocalDate.now()).getYears())
                .lastname(member.getLastname())
                .firstname(member.getFirstname())
                .roster(member.getRoster() != null ? member.getRoster().getName() : null)
                .nationality(member.getNationality())
                .pseudo(member.getPseudo())
                .image(member.getImage())
                .build();
    }

    public OpponentMemberDto toOpponentMemberDto(Member member) {
        return OpponentMemberDto.builder()
                .id(member.getId())
                .role(member.getRole())
                .lastname(member.getLastname())
                .firstname(member.getFirstname())
                .roster(member.getRoster() != null ? member.getRoster().getName() : null)
                .build();
    }

    public List<MemberDto> mapList(List<Member> members){
        return members.stream()
            .map(this::map)
                .sorted(Comparator.comparing(MemberDto::getRole))
                .collect(Collectors.toList());

    }

    public MemberShortDto toShortMemberDto(Member member) {
        return MemberShortDto.builder()
                .id(member.getId())
                .pseudo(member.getPseudo())
                .build();
    }
}
