package fr.teampeps.repository;

import fr.teampeps.models.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface PartnerRepository extends JpaRepository<Partner, String> {

    @Query("SELECT p FROM Partner p WHERE p.isActive = :isActive")
    List<Partner> findAllByIsActive(@PathVariable("isActive") boolean isActive);
}
