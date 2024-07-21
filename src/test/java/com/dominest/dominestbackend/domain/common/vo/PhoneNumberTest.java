package com.dominest.dominestbackend.domain.common.vo;



import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class PhoneNumberTest {

    @DisplayName("유효한 전화번호 형식으로 PhoneNumber 객체를 생성할 수 있다")
    @ParameterizedTest(name = "유효한 전화번호 형식 -> {0} ")
    @ValueSource(strings = {
            "010-0123-5678",
            "019-123-4567",
            "011-099-0099",
    })
    void should_CreatePhoneNumber_When_ValidPhoneNumberParam(String phoneNumberParam) {
        // given - parameter
        // when
        PhoneNumber phoneNumber = new PhoneNumber(phoneNumberParam);

        // then
        assertThat(phoneNumber.getValue()).isEqualTo(phoneNumberParam);
    }

    @DisplayName("PhoneNumber 객체 생성 시 유효한 전화번호 형식이 아닐 경우 예외가 발생한다")
    @ParameterizedTest(name = "유효하지 않은 전화번호 형식 -> {0} ")
    @NullAndEmptySource
    @ValueSource(strings = {
            "abc", "0", "-",
            "010-1234-567",
            "010-123-45678",
            "012-1234-5678",
            "015-1234-5678",
            "01012345678",
    })
    void should_ThrowEx_When_Invalid(String phoneNumberParam) {
        // given - parameter
        // when
        assertThatThrownBy(() -> new PhoneNumber(phoneNumberParam))
                //then
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 전화번호 형식입니다. 입력값 -> " + phoneNumberParam);
    }
}
