package fr.teampeps.repository;

import fr.teampeps.model.Heroe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface HeroeRepository extends JpaRepository<Heroe, String> {

    @Query("SELECT h FROM Heroe h WHERE h.game = ?1 ORDER BY h.name ASC")
    Set<Heroe> findAllByGameOrderByNameAsc(String game);
}
