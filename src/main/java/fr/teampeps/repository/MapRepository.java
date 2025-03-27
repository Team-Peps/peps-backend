package fr.teampeps.repository;

import fr.teampeps.model.map.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface MapRepository extends JpaRepository<Map, String> {

    @Query("SELECT m FROM Map m WHERE m.game = ?1 ORDER BY m.name ASC")
    Set<Map> findAllByGameOrderByNameAsc(String game);
}
