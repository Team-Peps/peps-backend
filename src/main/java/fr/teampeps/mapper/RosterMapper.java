package fr.teampeps.mapper;

import fr.teampeps.dto.MemberMediumDto;
import fr.teampeps.dto.RosterDto;
import fr.teampeps.dto.RosterMediumDto;
import fr.teampeps.dto.RosterShortDto;
import fr.teampeps.model.Roster;
import fr.teampeps.model.member.Member;
import fr.teampeps.utils.ImageUtils;
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

        try {
            roster.setImage(ImageUtils.decompressImage(roster.getImage()));
        } catch (Exception e) {
            log.error("Error decompressing image for roster with ID: {}", roster.getId(), e);
        }

        return RosterMediumDto.builder()
                .id(roster.getId())
                .name(roster.getName())
                .members(memberMapper.sortByRole(members))
                .game(roster.getGame())
                .image(roster.getImage())
                .matchCount(matchCount)
                .isOpponent(roster.getIsOpponent())
                .build();
    }

    public RosterShortDto toRosterShortDto(Roster roster, Long matchCount){

        try {
            roster.setImage(ImageUtils.decompressImage(roster.getImage()));
        } catch (Exception e) {
            log.error("Error decompressing image for roster with ID: {}", roster.getId(), e);
        }
        return RosterShortDto.builder()
                .id(roster.getId())
                .name(roster.getName())
                .game(roster.getGame())
                .matchCount(matchCount)
                .image(roster.getImage())
                .isOpponent(roster.getIsOpponent())
                .build();
    }
}
