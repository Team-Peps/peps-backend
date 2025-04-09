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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
}
