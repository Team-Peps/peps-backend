package fr.teampeps.model.user;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {

    ADMIN(0),
    STAFF(1),
    USER(2),
    LOCKED(3);

    final int role;

    Authority(int role){
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
