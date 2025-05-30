package fr.teampeps.repository;

import fr.teampeps.enums.Game;
import fr.teampeps.models.Heroe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HeroeRepository extends JpaRepository<Heroe, String> {

    @Query("SELECT h FROM Heroe h WHERE h.game = ?1 ORDER BY h.name ASC")
    List<Heroe> findAllByGameOrderByNameAsc(Game game);
}
