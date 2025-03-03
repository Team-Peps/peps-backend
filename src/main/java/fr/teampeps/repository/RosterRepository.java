package fr.teampeps.repository;

import fr.teampeps.model.Roster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface RosterRepository extends JpaRepository<Roster, String> {

    @Query("SELECT r FROM Roster r WHERE r.isOpponent = false")
    List<Roster> findAllPepsRosters();

    @Query("SELECT r FROM Roster r WHERE r.isOpponent = true")
    List<Roster> findAllOpponentRosters();
}
