package fr.teampeps.repository;

import fr.teampeps.model.Slider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SliderRepository extends JpaRepository<Slider, String> {

    List<Slider> findAllByIsActive(boolean b);
}
