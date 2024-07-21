package com.dominest.dominestbackend.domain.common.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class EmailTest {

    @DisplayName("유효한 이메일 형식으로 Email 객체를 생성할 수 있다")
    @ParameterizedTest(name = "유효한 이메일 형식 -> {0} ")
    @ValueSource(strings = {
            "userId@user.net",
            "join.user.name@example.dot.com",
            "username@gmail.com",
    })
    void should_CreateEmail_When_ValidEmailParam(String emailParam) {
        // given - parameter
        // when
        Email email = new Email(emailParam);

        // then
        assertThat(email.getValue()).isEqualTo(emailParam);
    }

    @DisplayName("Email 객체 생성 시 유효한 이메일 형식이 아닐 경우 예외가 발생한다")
    @ParameterizedTest(name = "유효하지 않은 이메일 형식 -> {0}")
    @NullAndEmptySource
    @ValueSource(strings = {
            "user@name@example.com",
            ".user@example.com",
            "user@example",
            "user@.com",
            "plain-address",
            "@no-local-part.com",
            // TLD
            "user@example.c",
            "user@example.abcdefgh",
            // empty
            "user name@example.com",
            "user@example com"
    })
    void should_ThrowEx_When_InvalidEmail(String emailParam) {
        // given - parameter
        // when
        assertThatThrownBy(() -> new Email(emailParam))
                //then
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 이메일 형식입니다. 입력값 -> " + emailParam);
    }
}
