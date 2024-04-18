package com.dominest.dominestbackend.domain.user.component;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER("User"),
    ROLE_ADMIN("Admin");

    private final String label;

    Role(String description) {
        this.label = description;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String getAuthority() {
        return label;
    }
}
