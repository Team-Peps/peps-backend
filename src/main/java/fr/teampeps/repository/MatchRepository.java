package fr.teampeps.repository;

import fr.teampeps.model.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, String > {

    @Query("SELECT COUNT(m) FROM Match m WHERE m.roster.id = :id OR m.opponentRoster.id = :id")
    Long countMatchesByRoster(@Param("id") String id);

    @Query("SELECT r.id,r .name, COUNT(m) FROM Roster r " +
            "LEFT JOIN Match m ON r.id = m.roster.id OR r.id = m.opponentRoster.id " +
            "GROUP BY r.id, r.name ORDER BY COUNT(m) DESC")
    List<Object[]> countMatchesPerRoster();
}
