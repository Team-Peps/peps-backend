package fr.teampeps.service;

import fr.teampeps.dto.MatchDto;
import fr.teampeps.dto.MatchGroupByDate;
import fr.teampeps.mapper.MatchMapper;
import fr.teampeps.model.Game;
import fr.teampeps.model.Match;
import fr.teampeps.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchMapper matchMapper;

    @InjectMocks
    private MatchService matchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllMatchesSortedByDateGroupByGame_shouldReturnMapGroupedByGame() {
        Match match = new Match();
        MatchDto dto = MatchDto.builder().build();
        when(matchRepository.findAllByGameByOrderByDatetimeDesc(Game.OVERWATCH)).thenReturn(List.of(match));
        when(matchRepository.findAllByGameByOrderByDatetimeDesc(Game.MARVEL_RIVALS)).thenReturn(List.of(match));
        when(matchMapper.toMatchDto(any())).thenReturn(dto);

        Map<String, List<MatchDto>> result = matchService.getAllMatchesSortedByDateGroupByGame();

        assertEquals(1, result.get("overwatch").size());
        assertEquals(1, result.get("marvel-rivals").size());
    }

    @Test
    void getCurrentMatch_shouldReturnMatchInCurrentWindow() {
        Match match = new Match();
        MatchDto dto = MatchDto.builder().build();
        when(matchRepository.findFirstByDatetimeBetweenAndScoreIsNullOrderByDatetimeAsc(any(), any())).thenReturn(Optional.of(match));
        when(matchMapper.toMatchDto(match)).thenReturn(dto);

        Optional<MatchDto> result = matchService.getCurrentMatch();

        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    void getNext5Matches_shouldGroupByDate() {
        Match match = new Match();
        match.setDatetime(LocalDateTime.now());
        MatchDto dto = MatchDto.builder().build();

        when(matchRepository.findAllByScoreIsNullOrderByDatetimeAsc()).thenReturn(List.of(match));
        when(matchMapper.toMatchDto(match)).thenReturn(dto);

        List<MatchGroupByDate> result = matchService.getNext5Matches();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0).matches().get(0));
    }

    @Test
    void getResultsMatches_shouldReturnGroupedPage() {
        Match match = new Match();
        match.setDatetime(LocalDateTime.now());
        MatchDto dto = MatchDto.builder().build();
        Page<Match> matchPage = new PageImpl<>(List.of(match));

        when(matchRepository.findAllByScoreIsNotNullAndGameInOrderByDatetimeDesc(anyList(), any())).thenReturn(matchPage);
        when(matchMapper.toMatchDto(match)).thenReturn(dto);

        Page<MatchGroupByDate> result = matchService.getResultsMatches(0, "OVERWATCH");

        assertEquals(1, result.getContent().size());
        assertEquals(dto, result.getContent().get(0).matches().get(0));
    }

    @Test
    void getUpcomingMatches_shouldReturnGroupedPage() {
        Match match = new Match();
        match.setDatetime(LocalDateTime.now());
        MatchDto dto = MatchDto.builder().build();
        Page<Match> matchPage = new PageImpl<>(List.of(match));

        when(matchRepository.findAllByScoreIsNullAndGameInOrderByDatetimeDesc(anyList(), any())).thenReturn(matchPage);
        when(matchMapper.toMatchDto(match)).thenReturn(dto);

        Page<MatchGroupByDate> result = matchService.getUpcomingMatches(0, "OVERWATCH");

        assertEquals(1, result.getContent().size());
        assertEquals(dto, result.getContent().get(0).matches().get(0));
    }

    @Test
    void getUpcomingMatchesByGame_shouldReturnGroupedMatches() {
        Match match = new Match();
        match.setDatetime(LocalDateTime.now());
        MatchDto dto = MatchDto.builder().build();

        when(matchRepository.findAllByGameAndScoreIsNullOrderByDatetimeDesc(Game.OVERWATCH)).thenReturn(List.of(match));
        when(matchMapper.toMatchDto(match)).thenReturn(dto);

        List<MatchGroupByDate> result = matchService.getUpcomingMatchesByGame(Game.OVERWATCH);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0).matches().get(0));
    }
}
