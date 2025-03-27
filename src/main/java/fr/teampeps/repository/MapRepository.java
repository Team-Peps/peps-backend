package fr.teampeps.repository;

import fr.teampeps.model.map.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapRepository extends JpaRepository<Map, String> {
}
