package fr.teampeps.mapper;

import fr.teampeps.dto.RosterDto;
import fr.teampeps.dto.RosterShortDto;
import fr.teampeps.model.Roster;
import fr.teampeps.model.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RosterMapper {

    private final MemberMapper memberMapper;

    public RosterDto map(Roster roster, List<Member> members){
        return RosterDto.builder()
                .id(roster.getId())
                .name(roster.getName())
                .members(memberMapper.mapList(members))
                .game(roster.getGame())
                .build();
    }

    public RosterShortDto mapShort(Roster roster, Long matchCount){
        return RosterShortDto.builder()
                .id(roster.getId())
                .name(roster.getName())
                .game(roster.getGame())
                .matchCount(matchCount)
                .build();
    }
}
