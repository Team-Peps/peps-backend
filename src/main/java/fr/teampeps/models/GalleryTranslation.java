package fr.teampeps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "gallery_translations")
public class GalleryTranslation implements Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "lang", nullable = false)
    private String lang;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "gallery_id", nullable = false)
    private Gallery parent;

    @Override
    public void setParent(TranslatableEntity<?> parent) {
        this.parent = (Gallery) parent;
    }
}
