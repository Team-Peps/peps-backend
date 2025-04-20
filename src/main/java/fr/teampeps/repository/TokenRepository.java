package fr.teampeps.repository;

import fr.teampeps.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByHex(String token);
    @Query("""
        SELECT t FROM Token t INNER JOIN User u ON t.user.id = u.id
        WHERE u.id = :userId AND (t.isExpired = false OR t.isRevoked = false)
    """)
    List<Token> findAllValidTokensByUser(String userId);

}
