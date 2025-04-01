package fr.teampeps.repository;

import fr.teampeps.model.Game;
import fr.teampeps.model.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, String > {

    @Query("SELECT COUNT(m) FROM Match m WHERE m.game = :game")
    Long countMatchesByGame(@Param("game") String game);

    @Query("SELECT m FROM Match m WHERE m.game = :game ORDER BY m.datetime DESC")
    List<Match> findAllByGameByOrderByDatetimeDesc(@PathVariable("game") Game game);

    @Query("SELECT m FROM Match m WHERE m.score IS NOT NULL AND m.id = :id")
    Optional<Match> isMatchScoreIsNotNull(@PathVariable("id") String id);
}
