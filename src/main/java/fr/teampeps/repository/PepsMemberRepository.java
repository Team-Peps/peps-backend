package fr.teampeps.repository;

import fr.teampeps.model.member.PepsMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PepsMemberRepository extends JpaRepository<PepsMember, String> {
}
