package fr.teampeps.repository;

import fr.teampeps.enums.Game;
import fr.teampeps.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, String> {

    @Query("SELECT m FROM Member m WHERE m.game = :game AND m.isSubstitute = false and m.isActive = true and m.role != 'COACH'")
    List<Member> findAllActiveHolderByGame(@PathVariable("game") Game game);

    @Query("SELECT m FROM Member m WHERE m.game = :game AND m.isSubstitute = true and m.isActive = true")
    List<Member> findAllActiveSubstituteByGame(@PathVariable("game") Game game);

    @Query("SELECT m FROM Member m WHERE m.game = :game AND m.role = 'COACH' AND m.isActive = true")
    List<Member> findAllActiveCoachByGame(@PathVariable("game") Game game);

    @Query("SELECT m FROM Member m WHERE m.game = :game AND m.isActive = false")
    List<Member> findAllInactiveByGame(Game game);
}
