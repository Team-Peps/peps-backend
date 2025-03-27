package fr.teampeps.repository;

import fr.teampeps.dto.MemberMediumDto;
import fr.teampeps.dto.MemberShortDto;
import fr.teampeps.model.Roster;
import fr.teampeps.model.member.Member;
import fr.teampeps.model.member.OpponentMember;
import fr.teampeps.model.member.PepsMember;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface MemberRepository extends JpaRepository<Member, String> {

    @Query("SELECT new fr.teampeps.dto.MemberShortDto(m.id, m.pseudo) FROM Member m WHERE m.roster.id = :rosterId")
    Set<MemberShortDto> findByRosterId(String rosterId);

    @Cacheable("memberCache")
    @Query("SELECT m FROM PepsMember m")
    List<PepsMember> findAllPepsMember();

    @Cacheable("memberCache")
    @Query("SELECT m FROM OpponentMember m")
    List<OpponentMember> findAllOpponentMember();

    @Query("SELECT m FROM Member m WHERE m.roster IS NULL")
    List<Member> findAllWithoutRoster();

    @Query("SELECT new fr.teampeps.dto.MemberMediumDto(m.id, m.pseudo, m.firstname, m.lastname, m.role) FROM Member m WHERE m.roster = :roster")
    List<MemberMediumDto> findMemberByRoster(Roster roster);

}
