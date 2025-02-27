package fr.teampeps.service;

import fr.teampeps.dto.RosterDto;
import fr.teampeps.dto.RosterShortDto;
import fr.teampeps.mapper.RosterMapper;
import fr.teampeps.repository.RosterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RosterService {

    private final RosterRepository rosterRepository;
    private final RosterMapper rosterMapper;

    public Set<RosterShortDto> getAllRosters() {
        return rosterRepository.findAll().stream()
                .map(rosterMapper::mapShort)
                .collect(java.util.stream.Collectors.toSet());
    }

    public RosterDto getRoster(String id) {
        return rosterMapper.map(rosterRepository.findById(id).orElseThrow());
    }
}
