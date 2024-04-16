package com.dominest.dominestbackend.global.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class PhoneNumberValidatorTest {
    @ParameterizedTest
    @ValueSource(strings = {"010-1234-5678", "016-123-4567", "019-123-4567"})
    @DisplayName("유효한 휴대전화번호 형식일 경우 true를 반환한다.")
    void isValid_validNumbers(String phoneNumber) {
        //given
        PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator();
        //when
        boolean valid = phoneNumberValidator.isValid(phoneNumber, null);
        //then
        assertThat(valid).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"01012345678", "010-12345-6789", "010-1234-56789"
            , "012-1234-5678", "010-1234-789", "abc-defg-hijk"})
    @DisplayName("유효하지 않은 휴대전화번호 형식일 경우 false를 반환한다.")
    void isValid_invalidNumbers(String phoneNumber) {
        //given
        PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator();
        //when
        boolean valid = phoneNumberValidator.isValid(phoneNumber, null);
        //then
        assertThat(valid).isFalse();
    }
}
