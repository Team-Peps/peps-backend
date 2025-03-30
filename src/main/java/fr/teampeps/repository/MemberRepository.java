package fr.teampeps.repository;

import fr.teampeps.model.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, String> {

    @Query("SELECT m FROM Member m WHERE m.isSubstitute = false and m.role != 'COACH'")
    List<Member> findAllActive();

    @Query("SELECT m FROM Member m WHERE m.isSubstitute = true")
    List<Member> findAllSubstitute();

    @Query("SELECT m FROM Member m WHERE m.role = 'COACH'")
    List<Member> findAllCoach();
}
