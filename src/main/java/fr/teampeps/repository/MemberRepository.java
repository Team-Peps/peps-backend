package fr.teampeps.repository;

import fr.teampeps.model.Roster;
import fr.teampeps.model.member.Member;
import fr.teampeps.model.member.OpponentMember;
import fr.teampeps.model.member.PepsMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, String> {

    @Query("SELECT m FROM Member m WHERE m.roster = :roster")
    List<Member> findByRoster(Roster roster);

    @Query("SELECT m FROM PepsMember m")
    List<PepsMember> findAllPepsMember();

    @Query("SELECT m FROM OpponentMember m")
    List<OpponentMember> findAllOpponentMember();

}
