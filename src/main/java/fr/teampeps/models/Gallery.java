package fr.teampeps.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "galleries")
@Builder
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "galleryCache")
@NoArgsConstructor
@AllArgsConstructor
public class Gallery extends TranslatableEntity<GalleryTranslation> {

    @Id
    @Column(name = "id",
            nullable = false)
    private String id;

    @Column(name = "date",
            nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "gallery", cascade = CascadeType.ALL, orphanRemoval = true)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "galleryPhotoCache")
    private List<GalleryPhoto> photos = new ArrayList<>();

    @Column(name = "thumbnail_image_key", nullable = false)
    private String thumbnailImageKey;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GalleryTranslation> translations = new ArrayList<>();
}
