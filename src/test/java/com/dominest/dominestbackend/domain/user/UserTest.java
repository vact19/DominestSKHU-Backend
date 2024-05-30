package com.dominest.dominestbackend.domain.user;

import com.dominest.dominestbackend.domain.common.vo.PhoneNumber;
import com.dominest.dominestbackend.domain.user.component.Role;
import com.dominest.dominestbackend.domain.common.vo.Email;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.global.exception.exceptions.auth.jwt.JwtAuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @DisplayName("refreshToken과 refreshTokenExp를 업데이트할 수 있다")
    @Test
    void updateRefreshTokenAndTokenExp() {
        // given
        User user = createUser();
        LocalDateTime refreshTokenExp = LocalDateTime.of(2021, 1, 1, 0, 0);
        // when
        user.updateRefreshTokenAndTokenExp("refreshToken", refreshTokenExp);
        // then
        assertThat(user.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(user.getRefreshTokenExp()).isEqualTo(refreshTokenExp);
    }

    @DisplayName("로그아웃 시 refreshToken은 빈 값, refreshTokenExp는 현재시간으로 설정된다")
    @Test
    void logout() {
        // given
        User user = createUser();
        LocalDateTime now = LocalDateTime.of(2021, 1, 1, 0, 0);
        // when
        user.logout(now);
        // then
        assertThat(user.getRefreshToken()).isEmpty();
        assertThat(user.getRefreshTokenExp()).isEqualTo(now);
    }

    @DisplayName("현재시간을 기준으로 refreshTokenExp가 만료되었을 경우 예외를 던진다")
    @Test
    void validateRefreshTokenExp_expired() {
        // given
        User user = createUser();
        LocalDateTime expiredRefreshTokenExp = LocalDateTime.of(2021, 1, 1, 0, 0);
        LocalDateTime now = LocalDateTime.of(2021, 2, 2, 2, 2);
        user.updateRefreshTokenAndTokenExp("refreshToken", expiredRefreshTokenExp);
        // when-then
        assertThatThrownBy(() -> user.validateRefreshTokenExp(now))
                .isInstanceOf(JwtAuthenticationException.class);
    }

    @DisplayName("현재시간을 기준으로 refreshTokenExp가 만료되지 않았을 경우 아무것도 하지 않는다")
    @Test
    void validateRefreshTokenExp_notExpired() {
        // given
        User user = createUser();
        LocalDateTime nonExpiredExp = LocalDateTime.of(2021, 2, 2, 2, 2);
        LocalDateTime now = LocalDateTime.of(2021, 1, 1, 0, 0);
        user.updateRefreshTokenAndTokenExp("refreshToken", nonExpiredExp);
        // when-then
        assertThatCode(() -> user.validateRefreshTokenExp(now))
                .doesNotThrowAnyException();
    }

    @DisplayName("비밀번호를 변경할 수 있다")
    @Test
    void changePassword() {
        // given
        User user = createUser();
        String newPassword = "newPassword";
        // when
        user.changePassword(newPassword);
        // then
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    private static User createUser() {
        return User.builder()
                .email(new Email("email@email.com"))
                .password("password")
                .name("김이름")
                .phoneNumber(new PhoneNumber("010-1234-5678"))
                .role(Role.ROLE_ADMIN) //  현재 모든 가입자는 관리자로 고정됨.
                .build();
    }
}
