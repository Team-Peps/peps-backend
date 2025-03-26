package fr.teampeps.service;

import fr.teampeps.dto.MemberMediumDto;
import fr.teampeps.dto.RosterMediumDto;
import fr.teampeps.dto.RosterShortDto;
import fr.teampeps.dto.RosterTinyDto;
import fr.teampeps.mapper.RosterMapper;
import fr.teampeps.model.Roster;
import fr.teampeps.repository.MatchRepository;
import fr.teampeps.repository.MemberRepository;
import fr.teampeps.repository.RosterRepository;
import fr.teampeps.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

    @Transactional
    public RosterMediumDto getRoster(String id) {
        Optional<Roster> roster = rosterRepository.findById(id);
        if (roster.isEmpty()) {
            throw new RuntimeException("Roster not found");
        }
        List<MemberMediumDto> memberList = memberRepository.findMemberByRoster(roster.get());

        return rosterMapper.toRosterMediumDto(roster.get(), memberList, matchRepository.countMatchesByRoster(id));
    }

    @Transactional
    public Set<RosterShortDto> getAllPepsRosters() {
        return rosterRepository.findAllPepsRosters().stream()
                .map(roster -> rosterMapper.toRosterShortDto(roster, matchRepository.countMatchesByRoster(roster.getId())))
                .collect(Collectors.toSet());
    }

    @Transactional
    public Set<RosterShortDto> getAllOpponentRosters() {
        return rosterRepository.findAllOpponentRosters().stream()
                .map(roster -> rosterMapper.toRosterShortDto(roster, matchRepository.countMatchesByRoster(roster.getId())))
                .collect(Collectors.toSet());
    }

    @Transactional
    public Set<RosterTinyDto> getAllPepsRostersTiny() {
        return rosterRepository.findAllRostersTinyWhereOpponent(false);
    }

    @Transactional
    public Set<RosterTinyDto> getAllOpponentRostersTiny() {
        return rosterRepository.findAllRostersTinyWhereOpponent(true);

    }

    @Transactional
    public RosterShortDto createRoster(Roster roster, MultipartFile imageFile) {
        try{
            Objects.requireNonNull(roster, "Roster cannot be null");

            roster.setNameLower(transformRosterName(roster.getName()));
            roster.setImage(ImageUtils.compressImage(imageFile.getBytes()));
            Roster savedRoster = rosterRepository.save(roster);

            log.info("✅ Roster created successfully with ID: {}", savedRoster.getId());

            return rosterMapper.toRosterShortDto(savedRoster, 0L);

        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid roster provided: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("❌ Database error while creating roster: {}", e.getMessage());
            throw new DataIntegrityViolationException("Database error while creating roster");
        } catch (Exception e) {
            log.error("❌ Unexpected error while creating roster: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while creating roster");
        }
    }

    @Transactional
    public void deleteRoster(String id) {
        try {
            Roster roster = rosterRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roster not found"));

            rosterRepository.delete(roster);
            log.info("✅ Roster deleted successfully with ID: {}", id);

        } catch (DataAccessException e) {
            log.error("❌ Database error while deleting roster: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error while deleting roster", e);
        } catch (Exception e) {
            log.error("❌ Unexpected error while deleting roster: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error while deleting roster", e);
        }
    }

    public RosterShortDto updateRoster(Roster roster, MultipartFile imageFile) {
        try {
            Objects.requireNonNull(roster, "Roster cannot be null");

            Roster existingRoster = rosterRepository.findById(roster.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roster not found"));

            if(imageFile != null){
                existingRoster.setImage(ImageUtils.compressImage(imageFile.getBytes()));
            }

            existingRoster.setName(roster.getName());
            existingRoster.setGame(roster.getGame());
            existingRoster.setNameLower(transformRosterName(roster.getName()));

            Roster updatedRoster = rosterRepository.save(existingRoster);

            log.info("✅ Roster updated successfully with ID: {}", roster.getId());

            Long matchCount = matchRepository.countMatchesByRoster(roster.getId());

            return rosterMapper.toRosterShortDto(updatedRoster, matchCount);

        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid roster provided: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid roster provided", e);
        } catch (DataIntegrityViolationException e) {
            log.error("❌ Database error while updating roster: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error while updating roster", e);
        } catch (Exception e) {
            log.error("❌ Unexpected error while updating roster: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error while updating roster", e);
        }

    }

    private String transformRosterName(String name) {
        return name.trim().toLowerCase();
    }
}
