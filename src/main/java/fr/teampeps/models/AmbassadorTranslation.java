package fr.teampeps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ambassador_translations")
public class AmbassadorTranslation implements Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "lang", nullable = false)
    private String lang;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "ambassador_id", nullable = false)
    private Ambassador parent;

    @Override
    public void setParent(TranslatableEntity<?> parent) {
        this.parent = (Ambassador) parent;
    }
}
