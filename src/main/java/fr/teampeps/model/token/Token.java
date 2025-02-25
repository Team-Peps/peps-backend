package fr.teampeps.model.token;

import fr.teampeps.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {

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
    private User user;
}
