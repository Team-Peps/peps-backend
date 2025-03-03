package fr.teampeps.service;

import fr.teampeps.dto.RosterDto;
import fr.teampeps.dto.RosterShortDto;
import fr.teampeps.mapper.RosterMapper;
import fr.teampeps.model.Roster;
import fr.teampeps.model.member.Member;
import fr.teampeps.repository.MemberRepository;
import fr.teampeps.repository.RosterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RosterService {

    private final RosterRepository rosterRepository;
    private final RosterMapper rosterMapper;
    private final MemberRepository memberRepository;

    public Set<RosterShortDto> getAllPepsRosters() {
        return rosterRepository.findAllPepsRosters().stream()
                .map(rosterMapper::mapShort)
                .collect(java.util.stream.Collectors.toSet());
    }

    public RosterDto getRoster(String id) {
        Optional<Roster> roster = rosterRepository.findById(id);
        if (roster.isEmpty()) {
            throw new RuntimeException("Roster not found");
        }
        List<Member> memberList = memberRepository.findByRoster(roster.get());

        return rosterMapper.map(roster.get(), memberList);
    }

    public Set<RosterShortDto> getAllOpponentRosters() {
        return rosterRepository.findAllOpponentRosters().stream()
                .map(rosterMapper::mapShort)
                .collect(java.util.stream.Collectors.toSet());
    }
}
