package fr.teampeps.mapper;

import fr.teampeps.dto.RosterDto;
import fr.teampeps.model.Roster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RosterMapper {

    private final MemberMapper memberMapper;
    private final GameMapper gameMapper;

    public RosterDto map(Roster roster){
        return RosterDto.builder()
                .id(roster.getId())
                .name(roster.getName())
                .members(memberMapper.mapList(roster.getMembers()))
                .game(gameMapper.map(roster.getGame()))
                .build();
    }

    public RosterDto mapShort(Roster roster){
        return RosterDto.builder()
                .id(roster.getId())
                .name(roster.getName())
                .game(gameMapper.map(roster.getGame()))
                .build();
    }
}
