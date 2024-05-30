package com.dominest.dominestbackend.domain.common.vo;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
public class PhoneNumber {

    private static final String PHONE_NUMBER_PATTERN =
            "(01[016789])-(\\d{3,4})-(\\d{4})";

    @Column(nullable = false, unique = true, name = "phone_number")
    private final String value;

    protected PhoneNumber() {
        this.value = null;
    }

    public PhoneNumber(String value) {
        if (value == null || ! value.matches(PHONE_NUMBER_PATTERN)) {
            throw new IllegalArgumentException("잘못된 전화번호 형식입니다. 입력값 -> " + value);
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
