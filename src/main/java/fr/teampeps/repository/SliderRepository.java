package fr.teampeps.repository;

import fr.teampeps.models.Slider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SliderRepository extends JpaRepository<Slider, String> {

    List<Slider> findAllByIsActive(boolean b);

    List<Slider> findAllByIsActiveOrderByOrder(Boolean isActive);
}
