package fr.teampeps.repository;

import fr.teampeps.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, String> {
    boolean existsAuthorByName(String name);
}
