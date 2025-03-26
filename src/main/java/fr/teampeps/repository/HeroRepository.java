package fr.teampeps.repository;

import fr.teampeps.model.Hero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeroRepository extends JpaRepository<Hero, String> {
}
