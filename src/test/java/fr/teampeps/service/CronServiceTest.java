package fr.teampeps.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import fr.teampeps.enums.Game;
import fr.teampeps.models.Match;
import fr.teampeps.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CronServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private CronService cronService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cronService, "urlStreamOverwatch", "https://www.twitch.tv/overwatchleague");
        ReflectionTestUtils.setField(cronService, "urlStreamMarvelRivals", "https://www.twitch.tv/marvelrivals");
    }

    @Test
    void testReformatString() throws Exception {
        // Test méthode privée via reflection
        java.lang.reflect.Method method = CronService.class.getDeclaredMethod("reformatString", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(cronService, "Team Peps - Finals");
        assertEquals("teampeps-finals", result);

        result = (String) method.invoke(cronService, "Some Complex! Name & Stuff");
        assertEquals("somecomplexnamestuff", result);
    }

    @Test
    void testGenerateMatchId() throws Exception {
        // Test méthode privée via reflection
        java.lang.reflect.Method method = CronService.class.getDeclaredMethod("generateMatchId", String.class, String.class, String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(cronService, "May 10, 2023", "overwatch", "Team Rival");
        assertEquals("overwatch-team-peps-vs-team rival-may 10, 2023", result);
    }

    @Test
    void testParseToDateTime_ValidDate() throws Exception {
        // Test méthode privée via reflection
        java.lang.reflect.Method method = CronService.class.getDeclaredMethod("parseToDateTime", String.class);
        method.setAccessible(true);

        LocalDateTime result = (LocalDateTime) method.invoke(cronService, "May 10, 2023 - 15:30 UTC");
        assertNotNull(result);
        assertEquals(2023, result.getYear());
        assertEquals(5, result.getMonthValue());
        assertEquals(10, result.getDayOfMonth());
        assertEquals(15, result.getHour());
        assertEquals(30, result.getMinute());
    }

    @Test
    void testParseToDateTime_InvalidDate() throws Exception {
        // Test méthode privée via reflection
        java.lang.reflect.Method method = CronService.class.getDeclaredMethod("parseToDateTime", String.class);
        method.setAccessible(true);

        assertThrows(InvocationTargetException.class, () -> method.invoke(cronService, "Invalid Date Format"));
    }

    @Test
    void testFetchAndSaveMatches() {

        // Création de quelques matchs de test
        Match upcomingOverwatch = Match.builder()
                .id("upcoming-overwatch")
                .game(Game.OVERWATCH)
                .datetime(LocalDateTime.now().plusDays(1))
                .build();

        Match upcomingMarvel = Match.builder()
                .id("upcoming-marvel")
                .game(Game.MARVEL_RIVALS)
                .datetime(LocalDateTime.now().plusDays(2))
                .build();

        Match playedOverwatch = Match.builder()
                .id("played-overwatch")
                .game(Game.OVERWATCH)
                .datetime(LocalDateTime.now().minusDays(1))
                .score("3")
                .opponentScore("1")
                .build();

        Match playedMarvel = Match.builder()
                .id("played-marvel")
                .game(Game.MARVEL_RIVALS)
                .datetime(LocalDateTime.now().minusDays(2))
                .score("2")
                .opponentScore("2")
                .build();

        // Création d'un spy pour pouvoir mocker les méthodes de la même classe
        CronService spyService = spy(cronService);

        doReturn(List.of(upcomingOverwatch))
                .when(spyService).fetchAndSaveUpcomingMatches(Game.OVERWATCH, "/overwatch/Team_Peps", "https://www.twitch.tv/overwatchleague");

        doReturn(List.of(upcomingMarvel))
                .when(spyService).fetchAndSaveUpcomingMatches(Game.MARVEL_RIVALS, "/marvelrivals/Team_Peps", "https://www.twitch.tv/marvelrivals");

        doReturn(List.of(playedOverwatch))
                .when(spyService).fetchAndSavePlayedMatches(Game.OVERWATCH, "/overwatch/Team_Peps/Played_Matches", "https://www.twitch.tv/overwatchleague");

        doReturn(List.of(playedMarvel))
                .when(spyService).fetchAndSavePlayedMatches(Game.MARVEL_RIVALS, "/marvelrivals/Team_Peps/Played_Matches", "https://www.twitch.tv/marvelrivals");

        // Exécution
        Map<String, List<Match>> result = spyService.fetchAndSaveMatches();

        assertNotNull(result);
        assertEquals(2, result.get("upcoming").size());
        assertEquals(2, result.get("played").size());

        assertTrue(result.get("upcoming").contains(upcomingOverwatch));
        assertTrue(result.get("upcoming").contains(upcomingMarvel));
        assertTrue(result.get("played").contains(playedOverwatch));
        assertTrue(result.get("played").contains(playedMarvel));
    }
}