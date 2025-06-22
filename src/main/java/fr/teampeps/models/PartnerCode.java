package fr.teampeps.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "partner_codes")
@NoArgsConstructor
@AllArgsConstructor
public class PartnerCode {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "code", nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Column(name = "description_fr", nullable = false, columnDefinition = "TEXT")
    private String descriptionFr;

    @Column(name = "description_en", nullable = false, columnDefinition = "TEXT")
    private String descriptionEn;
}
