package fr.teampeps.mapper;

import fr.teampeps.dto.*;
import fr.teampeps.model.Roster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RosterMapper {

    private final MemberMapper memberMapper;

    public RosterMediumDto toRosterMediumDto(Roster roster, List<MemberMediumDto> members, Long matchCount){
        return RosterMediumDto.builder()
                .id(roster.getId())
                .name(roster.getName())
                .members(memberMapper.sortByRole(members))
                .game(roster.getGame())
                .imageKey(roster.getImageKey())
                .matchCount(matchCount)
                .isOpponent(roster.getIsOpponent())
                .build();
    }

    public RosterShortDto toRosterShortDto(Roster roster, Long matchCount){
        return RosterShortDto.builder()
                .id(roster.getId())
                .name(roster.getName())
                .game(roster.getGame())
                .matchCount(matchCount)
                .imageKey(roster.getImageKey())
                .isOpponent(roster.getIsOpponent())
                .build();
    }

    public RosterTinyDto toRosterTinyDto(Roster roster){
        return RosterTinyDto.builder()
                .id(roster.getId())
                .name(roster.getName())
                .game(roster.getGame())
                .build();
    }
}
