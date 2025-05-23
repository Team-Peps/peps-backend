package fr.teampeps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "galleries")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "galleryCache")
public class Gallery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",
            nullable = false)
    private String id;

    @Column(name = "event_name",
            nullable = false)
    private String eventName;

    @Column(name = "date",
            nullable = false)
    private LocalDate date;

    @Column(name = "description",
            nullable = false, columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "gallery", cascade = CascadeType.ALL, orphanRemoval = true)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "galleryPhotoCache")
    private List<GalleryPhoto> photos = new ArrayList<>();

    @Column(name = "thumbnail_image_key", nullable = false)
    private String thumbnailImageKey;
}
