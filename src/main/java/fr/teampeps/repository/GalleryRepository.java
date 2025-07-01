package fr.teampeps.repository;

import fr.teampeps.enums.ArticleType;
import fr.teampeps.models.Article;
import fr.teampeps.models.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, String> {
    @Query("SELECT g FROM Gallery g ORDER BY g.date ASC")
    List<Gallery> findAllOrderByDate();

}
