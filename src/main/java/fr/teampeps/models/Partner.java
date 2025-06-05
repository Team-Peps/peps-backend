package fr.teampeps.models;

import fr.teampeps.enums.PartnerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Getter
@Setter
@Table(name = "partners")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "partnerCache")
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",
            nullable = false)
    private String id;

    @Column(name = "name",
            nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_key")
    private String imageKey;

    @Column(name = "link")
    private String link;

    @Column(name = "codes")
    private String codes;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "order_index", nullable = false)
    private Long order;

    @Enumerated(EnumType.STRING)
    @Column(name = "partner_type", nullable = false)
    private PartnerType type = PartnerType.MINOR;
}
