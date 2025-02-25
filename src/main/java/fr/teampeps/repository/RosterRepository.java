package fr.teampeps.repository;

import fr.teampeps.model.Roster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RosterRepository extends JpaRepository<Roster, String> {
}
