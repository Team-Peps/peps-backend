package fr.teampeps.repository;

import fr.teampeps.model.Achievement;
import fr.teampeps.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, String> {

    @Query("SELECT a FROM Achievement a WHERE a.game = :game AND a.member IS NULL")
    List<Achievement> findAllByGame(Game game);

    List<Achievement> findAllByMemberId(String memberId);
}
