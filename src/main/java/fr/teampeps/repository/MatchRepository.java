package fr.teampeps.repository;

import fr.teampeps.model.Game;
import fr.teampeps.model.Match;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, String > {

    @Query("SELECT m FROM Match m WHERE m.game = :game ORDER BY m.datetime DESC")
    List<Match> findAllByGameByOrderByDatetimeDesc(@PathVariable("game") Game game);

    @Query("SELECT m FROM Match m WHERE m.score IS NULL AND m.id = :id")
    Optional<Match> isMatchScoreIsNull(@PathVariable("id") String id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Match m WHERE m.score IS NULL")
    void deleteAllWhereScoreIsNull();

    Optional<Match> findFirstByDatetimeBetweenAndScoreIsNullOrderByDatetimeAsc(LocalDateTime start, LocalDateTime end);

    @Query("SELECT m FROM Match m WHERE m.score IS NULL ORDER BY m.datetime ASC LIMIT 5")
    List<Match> findAllByScoreIsNullOrderByDatetimeAsc();

    Page<Match> findAllByScoreIsNotNullAndGameInOrderByDatetimeDesc(List<Game> games, Pageable pageable);

    Page<Match> findAllByScoreIsNullAndGameInOrderByDatetimeDesc(List<Game> games, Pageable pageable);

    List<Match> findAllByGameAndScoreIsNullOrderByDatetimeDesc(Game game);
}
