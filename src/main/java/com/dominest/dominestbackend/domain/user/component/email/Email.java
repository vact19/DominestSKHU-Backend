package com.dominest.dominestbackend.domain.user.component.email;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Getter
@Embeddable
public class Email {

    @Column(nullable = false, unique = true)
    private final String value;

    protected Email() {
        this.value = null;
        throw new UnsupportedOperationException("빈 생성자 호출 불가");
    }

    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private void validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("잘못된 이메일 형식입니다. 입력값 -> " + email);
        }
    }

    public Email(String email) {
        validateEmail(email);
        this.value = email;
    }
}
