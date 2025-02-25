package fr.teampeps.mapper;

import fr.teampeps.dto.MemberDto;
import fr.teampeps.model.Member;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberMapper {

    public MemberDto map(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .role(member.getRole())
                .dpi(member.getDpi())
                .dateOfBirth(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(member.getDateOfBirth()))
                .age(member.getDateOfBirth().until(LocalDate.now()).getYears())
                .lastname(member.getLastname())
                .firstname(member.getFirstname())
                .nationality(member.getNationality())
                .pseudo(member.getPseudo())
                .build();
    }

    public List<MemberDto> mapList(List<Member> members){
        return members.stream()
            .map(this::map)
            .sorted(Comparator.comparing(MemberDto::getRole))
            .collect(Collectors.toList());
    }
}
