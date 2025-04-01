package fr.teampeps.service;

import fr.teampeps.dto.MatchDto;
import fr.teampeps.mapper.MatchMapper;
import fr.teampeps.model.Game;
import fr.teampeps.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

}
