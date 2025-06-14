package fr.teampeps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "slider_translations")
public class SliderTranslation implements Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "lang", nullable = false)
    private String lang;

    @Column(name = "cta_label", nullable = false)
    private String ctaLabel;

    @Column(name = "mobile_image_key", nullable = false)
    private String mobileImageKey;

    @Column(name = "image_key", nullable = false)
    private String imageKey;

    @ManyToOne
    @JoinColumn(name = "slider_id", nullable = false)
    private Slider parent;

    @Override
    public void setParent(TranslatableEntity<?> parent) {
        this.parent = (Slider) parent;
    }

}
