package com.dominest.dominestbackend.domain.common.vo;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
public class Email {

    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @Column(nullable = false, unique = true)
    private final String email;

    protected Email() {
        this.email = null;
    }

    public Email(String email) {
        if (email == null || ! email.matches(EMAIL_PATTERN)) {
            throw new IllegalArgumentException("잘못된 이메일 형식입니다. 입력값 -> " + email);
        }
        this.email = email;
    }
}
