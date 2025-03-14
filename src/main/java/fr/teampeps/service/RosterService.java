package fr.teampeps.service;

import fr.teampeps.dto.RosterDto;
import fr.teampeps.dto.RosterShortDto;
import fr.teampeps.exceptions.DatabaseException;
import fr.teampeps.mapper.RosterMapper;
import fr.teampeps.model.Roster;
import fr.teampeps.model.member.Member;
import fr.teampeps.repository.MatchRepository;
import fr.teampeps.repository.MemberRepository;
import fr.teampeps.repository.RosterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RosterService {

    private final RosterRepository rosterRepository;
    private final RosterMapper rosterMapper;
    private final MemberRepository memberRepository;
    private final MatchRepository matchRepository;

    public Set<RosterShortDto> getAllPepsRosters() {
        return rosterRepository.findAllPepsRosters().stream()
                .map(roster -> rosterMapper.mapShort(roster, matchRepository.countMatchesByTeam(roster.getId())))
                .collect(Collectors.toSet());
    }

    @Transactional
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
                .map(roster -> rosterMapper.mapShort(roster, matchRepository.countMatchesByTeam(roster.getId())))
                .collect(Collectors.toSet());
    }

    public RosterShortDto createOpponentRoster(Roster roster) {
        try{
            Objects.requireNonNull(roster, "Roster cannot be null");

            roster.setIsOpponent(true);
            roster.setNameLower(transformRosterName(roster.getName()));
            Roster savedRoster = rosterRepository.save(roster);

            log.info("✅ Opponent roster created successfully with ID: {}", savedRoster.getId());

            return rosterMapper.mapShort(savedRoster, 0L);

        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid roster provided: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("❌ Database error while creating opponent roster: {}", e.getMessage());
            throw new DataIntegrityViolationException("Database error while creating opponent roster");
        } catch (Exception e) {
            log.error("❌ Unexpected error while creating opponent roster: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while creating opponent roster");
        }
    }

    @Transactional
    public void deleteOpponentRoster(String id) {
        try {
            Roster roster = rosterRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roster not found"));

            rosterRepository.delete(roster);
            log.info("✅ Opponent roster deleted successfully with ID: {}", id);

        } catch (DataAccessException e) {
            log.error("❌ Database error while deleting opponent roster: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error while deleting opponent roster", e);
        } catch (Exception e) {
            log.error("❌ Unexpected error while deleting opponent roster: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error while deleting opponent roster", e);
        }
    }

    public RosterShortDto updateOpponentRoster(String id, Roster roster) {
        try {
            Objects.requireNonNull(roster, "Roster cannot be null");

            Roster existingRoster = rosterRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roster not found"));

            existingRoster.setName(roster.getName());
            existingRoster.setGame(roster.getGame());
            existingRoster.setNameLower(transformRosterName(roster.getName()));

            Roster updatedRoster = rosterRepository.save(existingRoster);

            log.info("✅ Opponent roster updated successfully with ID: {}", id);

            return rosterMapper.mapShort(updatedRoster, 0L);

        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid roster provided: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid roster provided", e);
        } catch (DataIntegrityViolationException e) {
            log.error("❌ Database error while updating opponent roster: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error while updating opponent roster", e);
        } catch (Exception e) {
            log.error("❌ Unexpected error while updating opponent roster: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error while updating opponent roster", e);
        }

    }

    private String transformRosterName(String name) {
        return name.trim().toLowerCase();
    }
}
