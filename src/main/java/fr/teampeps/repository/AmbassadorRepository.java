package fr.teampeps.repository;

import fr.teampeps.models.Ambassador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorRepository extends JpaRepository<Ambassador, String> {
}
