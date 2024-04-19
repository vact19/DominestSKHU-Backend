package com.dominest.dominestbackend.domain.common.vo;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
public class PhoneNumber {

    private static final String PHONE_NUMBER_PATTERN =
            "(01[016789])-(\\d{3,4})-(\\d{4})";

    @Column(nullable = false, unique = true)
    private final String phoneNumber;

    protected PhoneNumber() {
        this.phoneNumber = null;
    }

    public PhoneNumber(String phoneNumber) {
        if (phoneNumber == null || ! phoneNumber.matches(PHONE_NUMBER_PATTERN)) {
            throw new IllegalArgumentException("잘못된 전화번호 형식입니다. 입력값 -> " + phoneNumber);
        }
        this.phoneNumber = phoneNumber;
    }
}
