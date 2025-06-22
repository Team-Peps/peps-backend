package fr.teampeps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "member_translations")
public class MemberTranslation implements Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "lang", nullable = false)
    private String lang;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member parent;

    @Override
    public void setParent(TranslatableEntity<?> parent) {
        this.parent = (Member) parent;
    }
}
