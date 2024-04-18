package com.dominest.dominestbackend.domain.jwt.constant;

import lombok.Getter;

@Getter
public enum AuthScheme {

    BEARER("Bearer");

    AuthScheme(String label) {
        this.label = label;
    }

    private final String label;
}
