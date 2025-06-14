package fr.teampeps.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "sliders", indexes = {
        @Index(name = "idx_slider_order", columnList = "order_index")
})
@Cacheable
@Builder
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "sliderCache")
@NoArgsConstructor
@AllArgsConstructor
public class Slider extends TranslatableEntity<SliderTranslation>{

    @Id
    @Column(name = "id",
            nullable = false)
    private String id;

    @Column(name = "is_active",
            nullable = false)
    private Boolean isActive;

    @Column(name = "cta_link",
            nullable = false)
    private String ctaLink;

    @Column(name = "order_index",
            nullable = false)
    private Long order;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SliderTranslation> translations = new ArrayList<>();

}
