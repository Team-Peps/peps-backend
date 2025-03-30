package fr.teampeps.repository;

import fr.teampeps.model.Game;
import fr.teampeps.model.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, String> {

    @Query("SELECT m FROM Member m WHERE m.game = :game AND m.isSubstitute = false and m.role != 'COACH'")
    List<Member> findAllActiveByGame(@PathVariable("game") Game game);

    @Query("SELECT m FROM Member m WHERE m.game = :game AND m.isSubstitute = true")
    List<Member> findAllSubstituteByGame(@PathVariable("game") Game game);

    @Query("SELECT m FROM Member m WHERE m.game = :game AND m.role = 'COACH'")
    List<Member> findAllCoachByGame(@PathVariable("game") Game game);
}
