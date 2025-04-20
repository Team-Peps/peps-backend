package fr.teampeps.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.teampeps.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class Token implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id",
            nullable = false)
    private Integer id;

    @Column(name = "hex",
            nullable = false)
    private String hex;

    @Column(name = "type",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private TokenType type;

    @Column(name = "is_expired",
            nullable = false)
    private boolean isExpired;

    @Column(name = "is_revoked",
            nullable = false)
    private boolean isRevoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
