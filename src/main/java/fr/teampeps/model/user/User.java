package fr.teampeps.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @Column(name = "id",
            nullable = false)
    @UuidGenerator
    private String id;

    @Column(name = "username",
            nullable = false,
            unique=true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email",
            nullable = false,
            unique=true)
    private String email;

    @Column(name = "discord_id",
            unique=true)
    private String discordId = null;

    @Column(name = "avatar_url")
    private String avatarUrl = null;

    @Column(name = "auth_type")
    @Enumerated(EnumType.STRING)
    private AuthType authType;

    @Enumerated(EnumType.STRING)
    @Column(name = "authorities",
            nullable = false)
    private List<Authority> authorities;

    @Column(name = "enable",
            nullable = false)
    private Boolean enable = false;

    @CreationTimestamp
    @Column(name = "created_at",
            nullable = false, updatable = false)
    private LocalDate createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        for (Authority authority : this.authorities) {
            authorityList.add(new SimpleGrantedAuthority(authority.name()));
        }
        return authorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }

}
