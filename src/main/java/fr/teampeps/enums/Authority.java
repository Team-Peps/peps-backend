package fr.teampeps.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {

    ADMIN(0),
    USER(10);

    final int role;

    Authority(int role){
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
