package fr.teampeps.models;

import fr.teampeps.enums.PartnerType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "partners")
@Builder
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "partnerCache")
@NoArgsConstructor
@AllArgsConstructor
public class Partner extends TranslatableEntity<PartnerTranslation> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",
            nullable = false)
    private String id;

    @Column(name = "name",
            nullable = false)
    private String name;

    @Column(name = "image_key")
    private String imageKey;

    @Column(name = "link")
    private String link;

    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnerCode> codes = new ArrayList<>();

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "order_index", nullable = false)
    private Long order;

    @Enumerated(EnumType.STRING)
    @Column(name = "partner_type", nullable = false)
    private PartnerType type = PartnerType.MINOR;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnerTranslation> translations = new ArrayList<>();
}
