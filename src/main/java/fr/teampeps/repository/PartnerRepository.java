package fr.teampeps.repository;

import fr.teampeps.enums.PartnerType;
import fr.teampeps.models.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;
import java.util.List;

public interface PartnerRepository extends JpaRepository<Partner, String> {

    @Query("SELECT p FROM Partner p WHERE p.isActive = :isActive AND p.type = :partnerType ORDER BY p.order ASC")
    List<Partner> findAllByIsActiveAndPartnerType(@PathVariable("isActive") boolean isActive, @PathVariable("partnerType") PartnerType partnerType);

    @Query("SELECT p FROM Partner p WHERE p.isActive = :isActive ORDER BY p.order ASC")
    List<Partner> findAllByIsActive(@PathVariable("isActive") boolean isActive);
}
