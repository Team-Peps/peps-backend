package fr.teampeps.service;

import fr.teampeps.model.Roster;
import fr.teampeps.model.match.MapMatch;
import fr.teampeps.model.match.Match;
import fr.teampeps.model.match.TeamMatch;
import fr.teampeps.model.record.MatchCreateFinishedRequest;
import fr.teampeps.model.record.MatchListItemResponse;
import fr.teampeps.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final RosterRepository rosterRepository;
    private final MemberRepository memberRepository;

    public boolean createUpcomingMatch(Match match) {
        log.info("Creating match {}", match);
        try {
            matchRepository.save(match);
            return true;
        } catch (Exception e) {
            log.error("Error creating match", e);
            return false;
        }
    }

    @Transactional
    public boolean createFinishedMatch(MatchCreateFinishedRequest request) {

        Roster roster = rosterRepository.findById(request.roster())
                .orElseThrow(() -> new EntityNotFoundException("Roster Peps introuvable"));

        Roster opponent = rosterRepository.findById(request.opponentRoster())
                .orElseThrow(() -> new EntityNotFoundException("Roster adverse introuvable"));

        Match match = Match.builder()
                .competitionName(request.competitionName())
                .date(LocalDateTime.parse(request.datetime()))
                .type(request.type())
                .roster(roster)
                .opponentRoster(opponent)
                .score(request.score())
                .opponentScore(request.opponentScore())
                .build();

        List<MapMatch> maps = request.rounds().stream().map(round -> MapMatch.builder()
                .rounds(round.round())
                .gameCode(round.gameCode())
                .score(round.scorePeps())
                .opponentScore(round.scoreOpponent())
                .heroBan(round.bannedHeroPeps())
                .opponentHeroBan(round.bannedHeroOpponent())
                .map(round.mapId())
                .match(match)
                .build()).toList();

        log.info(maps.toString());

        Set<TeamMatch> teamMatchesPeps = mapPlayersToTeamMatches(request.pepsPlayers(), match);
        Set<TeamMatch> teamMatchesOpponent = mapPlayersToTeamMatches(request.opponentPlayers(), match);

        Set<TeamMatch> teamMatches = Stream.concat(teamMatchesPeps.stream(), teamMatchesOpponent.stream())
                .collect(Collectors.toSet());

        match.setMaps(maps);
        match.setTeamMatches(teamMatches);

        matchRepository.save(match);

        return true;
    }

    @Transactional
    public List<MatchListItemResponse> getAllMatchesSortedByDate() {
        return matchRepository.findAllByOrderByDateDesc().stream()
                .map(this::mapToMatchListItem)
                .toList();
    }

    private MatchListItemResponse mapToMatchListItem(Match match) {
        return new MatchListItemResponse(
                match.getId(),
                match.getDate(),
                match.getCompetitionName(),
                match.getType().name(),
                match.getRoster().getName(),
                match.getOpponentRoster().getName(),
                match.getRoster().getGame(),
                match.getScore(),
                match.getOpponentScore()
        );
    }

    private Set<TeamMatch> mapPlayersToTeamMatches(Set<String> playerIds, Match savedMatch) {
        return playerIds.stream()
                .map(playerId -> memberRepository.findById(playerId).orElse(null))
                .filter(Objects::nonNull)
                .map(member -> TeamMatch.builder()
                        .roster(member.getRoster())
                        .member(member)
                        .isSubstitute(false)
                        .match(savedMatch)
                        .build())
                .collect(Collectors.toSet());
    }

}
