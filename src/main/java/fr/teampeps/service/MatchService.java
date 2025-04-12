package fr.teampeps.service;

import fr.teampeps.dto.MatchDto;
import fr.teampeps.dto.MatchGroupByDate;
import fr.teampeps.mapper.MatchMapper;
import fr.teampeps.model.Game;
import fr.teampeps.model.Match;
import fr.teampeps.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    @Transactional
    public Map<String, List<MatchDto>> getAllMatchesSortedByDateGroupByGame() {

        List<MatchDto> overwatchMatches = matchRepository.findAllByGameByOrderByDatetimeDesc(Game.OVERWATCH)
                .stream()
                .map(matchMapper::toMatchDto)
                .toList();

        List<MatchDto> marvelRivalsMatch = matchRepository.findAllByGameByOrderByDatetimeDesc(Game.MARVEL_RIVALS)
                .stream()
                .map(matchMapper::toMatchDto)
                .toList();

        return Map.of(
                "overwatch", overwatchMatches,
                "marvel-rivals", marvelRivalsMatch
        );
    }

    public Optional<MatchDto> getCurrentMatch() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        return matchRepository.findFirstByDatetimeBetweenAndScoreIsNullOrderByDatetimeAsc(start, end).stream().map(matchMapper::toMatchDto)
                .findFirst();
    }

    public List<MatchGroupByDate> getNext5Matches() {
        List<Match> matches = matchRepository.findAllByScoreIsNullOrderByDatetimeAsc();

        Map<LocalDate, List<Match>> matchesByDate = matches.stream()
                .collect(Collectors.groupingBy(
                        match -> match.getDatetime().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return matchesByDate.entrySet().stream()
                .map(entry -> new MatchGroupByDate(entry.getKey(), entry.getValue()
                        .stream()
                        .map(matchMapper::toMatchDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public Page<MatchGroupByDate> getResultsMatches(int page, String filter) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("datetime").descending());

        List<Game> games = Arrays.stream(filter.split(","))
                .map(String::trim)
                .filter(Game::contains)
                .map(Game::valueOf)
                .toList();

        // Étape 1 : paginer les matchs scorés
        Page<Match> matchPage = matchRepository.findAllByScoreIsNotNullAndGameInOrderByDatetimeDesc(games, pageable);
        List<Match> matches = matchPage.getContent();

        // Étape 2 : grouper les matchs paginés par date
        Map<LocalDate, List<Match>> matchesByDate = matches.stream()
                .collect(Collectors.groupingBy(
                        match -> match.getDatetime().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Étape 3 : transformer chaque groupe en MatchGroupByDate
        List<MatchGroupByDate> grouped = matchesByDate.entrySet().stream()
                .map(entry -> new MatchGroupByDate(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(matchMapper::toMatchDto)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        // Étape 4 : retourner une Page avec les groupes créés à partir des matchs paginés
        return new PageImpl<>(grouped, pageable, matchPage.getTotalElements());
    }


    public Page<MatchGroupByDate> getUpcomingMatches(int page, String filter) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("datetime").descending());

        List<Game> games = Arrays.stream(filter.split(","))
                .map(String::trim)
                .filter(Game::contains)
                .map(Game::valueOf)
                .toList();

        // Étape 1 : paginer les matchs scorés
        Page<Match> matchPage = matchRepository.findAllByScoreIsNullAndGameInOrderByDatetimeDesc(games, pageable);
        List<Match> matches = matchPage.getContent();

        // Étape 2 : grouper les matchs paginés par date
        Map<LocalDate, List<Match>> matchesByDate = matches.stream()
                .collect(Collectors.groupingBy(
                        match -> match.getDatetime().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Étape 3 : transformer chaque groupe en MatchGroupByDate
        List<MatchGroupByDate> grouped = matchesByDate.entrySet().stream()
                .map(entry -> new MatchGroupByDate(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(matchMapper::toMatchDto)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        // Étape 4 : retourner une Page avec les groupes créés à partir des matchs paginés
        return new PageImpl<>(grouped, pageable, matchPage.getTotalElements());
    }
}
