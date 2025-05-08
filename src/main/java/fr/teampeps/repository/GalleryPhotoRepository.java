package fr.teampeps.repository;

import fr.teampeps.models.GalleryPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryPhotoRepository extends JpaRepository<GalleryPhoto, String > {
}
