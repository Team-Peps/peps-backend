package fr.teampeps.repository;

import fr.teampeps.model.Ambassador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorRepository extends JpaRepository<Ambassador, String> {
}
