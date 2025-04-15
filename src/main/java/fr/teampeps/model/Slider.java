package fr.teampeps.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Getter
@Setter
@Table(name = "sliders", indexes = {
        @Index(name = "idx_slider_order", columnList = "order_index")
})
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "sliderCache")
public class Slider {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",
            nullable = false)
    private String id;

    @Column(name = "image_key",
            nullable = false)
    private String imageKey;

    @Column(name = "mobile_image_key",
            nullable = false)
    private String mobileImageKey;

    @Column(name = "is_active",
            nullable = false)
    private Boolean isActive;

    @Column(name = "cta_link",
            nullable = false)
    private String ctaLink;

    @Column(name = "cta_label",
            nullable = false)
    private String ctaLabel;

    @Column(name = "order_index",
            nullable = false)
    private Long order;
}
