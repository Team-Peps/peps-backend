package fr.teampeps.repository;

import fr.teampeps.dto.RosterTinyDto;
import fr.teampeps.model.Roster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface RosterRepository extends JpaRepository<Roster, String> {

    @Query("SELECT r FROM Roster r WHERE r.isOpponent = false")
    List<Roster> findAllPepsRosters();

    @Query("SELECT r FROM Roster r WHERE r.isOpponent = true")
    List<Roster> findAllOpponentRosters();

    @Query("SELECT new fr.teampeps.dto.RosterTinyDto(r.id, r.name, r.game) FROM Roster r WHERE r.isOpponent = :isOpponent")
    Set<RosterTinyDto> findAllRostersTinyWhereOpponent(@Param("isOpponent") boolean isOpponent);
}
