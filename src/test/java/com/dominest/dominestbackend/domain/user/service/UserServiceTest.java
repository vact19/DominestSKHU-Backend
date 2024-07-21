package com.dominest.dominestbackend.domain.user.service;

import com.dominest.dominestbackend.api.user.request.JoinRequest;
import com.dominest.dominestbackend.domain.common.vo.Email;
import com.dominest.dominestbackend.domain.common.vo.PhoneNumber;
import com.dominest.dominestbackend.domain.jwt.dto.TokenDto;
import com.dominest.dominestbackend.domain.jwt.service.TokenManager;
import com.dominest.dominestbackend.domain.user.component.Role;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.repository.UserJpaRepository;
import com.dominest.dominestbackend.domain.user.repository.UserRepository;
import com.dominest.dominestbackend.global.config.security.SecurityConst;
import com.dominest.dominestbackend.global.exception.exceptions.auth.jwt.JwtAuthenticationException;
import com.dominest.dominestbackend.global.exception.exceptions.business.BusinessException;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import com.dominest.dominestbackend.global.util.ClockHolder;
import com.dominest.dominestbackend.global.util.DateConverter;
import com.dominest.dominestbackend.global.util.mock.FixedClockHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserServiceTest {

    @Autowired UserService userService;
    @Autowired UserJpaRepository userJpaRepository;
    @Autowired UserRepository userRepository;
    @Autowired TokenManager tokenManager;

    private static final LocalDateTime fixedTime = LocalDateTime.of(1970, 1, 1, 0, 0);
    @TestConfiguration
    static class TestConfig {
        // 테스트용 ClockHolder 고정시간 설정
        @Bean
        public ClockHolder clockHolder() {
            return new FixedClockHolder(fixedTime);
        }
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }

    final String dummyEmail = "email@example.com";
    final String dummyPassword = "1234";
    final String dummyName = "name";
    final String dummyPhoneNumber = "010-0100-1234";

    @DisplayName("JoinRequest 객체로 회원가입을 할 수 있다")
    @Test
    void when_validJoinRequest_should_join() {
        //given
        JoinRequest request = new JoinRequest("email@example.com", "1234", "name", "010-0100-0100");

        //when
        User savedUser = userService.save(request);

        //then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail().getValue()).isEqualTo(request.getEmail());
        assertThat(savedUser.getPassword()).isNotEqualTo(request.getPassword());
        assertThat(savedUser.getName()).isEqualTo(request.getName());
        assertThat(savedUser.getPhoneNumber().getValue()).isEqualTo(request.getPhoneNumber());
    }

    @DisplayName("회원가입 시 이메일 OR 전화번호가 유효하지 않다면 예외가 발생한다")
    @Test
    void when_inValidJoinRequest_should_throwEx() {
        //given
        String invalidValue = "INVALID_VALUE";
        JoinRequest request = new JoinRequest(invalidValue, "1234", "name", invalidValue);

        //when
        assertThatThrownBy(() -> userService.save(request))
                // then
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록된 사용자의 이메일과 비밀번호로 로그인하여 인증 토큰을 발급받을 수 있다")
    @Test
    void when_validEmailAndPassword_should_returnAuthenticationToken() {
        //given
        User savedUser = saveDummyUser();
        String email = savedUser.getEmail().getValue();
        String password = "1234"; // 암호화 전 비밀번호

        //when
        TokenDto tokenDto = userService.login(email, password);

        //then
        refreshTokenShouldUpdated(tokenDto, savedUser);
        tokenDtoShouldHaveValidUserInfo(tokenDto, savedUser);
        tokenDtoShouldHaveValidAudience(tokenDto, savedUser);
    }

    @DisplayName("등록되지 않은 이메일로 로그인할 경우 예외가 발생한다")
    @Test
    void when_loginWithUnregisteredEmail_should_ThrowEx() {
        //given
        String unregisteredEmail = "unregistered@not.registered";

        //when
        assertThatThrownBy(() -> userService.login(unregisteredEmail, null))
                // then
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("잘못된 비밀번호로 로그인할 경우 예외가 발생한다")
    @Test
    void when_loginWithWrongPassword_should_ThrowEx() {
        //given
        User savedUser = saveDummyUser();
        String email = savedUser.getEmail().getValue();
        String wrongPassword = "wrongPassword";

        //when
        assertThatThrownBy(() -> userService.login(email, wrongPassword))
                // then
                .isInstanceOf(BusinessException.class)
                .hasMessage("잘못된 로그인 정보입니다.");
    }

    // refresh Token 없는 경우, 있지만 토큰 만료 경우.
    @DisplayName("유효한 refresh token으로 토큰을 재발급받을 수 있다")
    @Test
    void when_validRefreshToken_should_reissueAuthenticationToken() {
        //given
        User savedUser = saveDummyUser();
        String refreshToken = userService.login(savedUser.getEmail().getValue(), "1234").getRefreshToken();

        //when
        TokenDto reissuedRefreshToken = userService.reissueByRefreshToken(refreshToken);

        //then
        refreshTokenShouldUpdated(reissuedRefreshToken, savedUser);
        tokenDtoShouldHaveValidUserInfo(reissuedRefreshToken, savedUser);
        tokenDtoShouldHaveValidAudience(reissuedRefreshToken, savedUser);
    }

    @DisplayName("refresh token으로 사용자를 찾을 수 없다면 예외가 발생한다")
    @Test
    void when_unregisteredRefreshToken_should_throwEx() {
        //given
        String unregisteredRefreshToken = "unregisteredRefreshToken";

        //when
        assertThatThrownBy(() -> userService.reissueByRefreshToken(unregisteredRefreshToken))
                //then
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("만료된 refresh token으로 재발급을 시도할 경우 예외가 발생한다")
    @Test
    void when_expiredRefreshToken_should_throwEx() {
        //given
        User user = User.builder()
                .email(new Email(dummyEmail))
                .password(dummyPassword)
                .name(dummyName)
                .phoneNumber(new PhoneNumber(dummyPhoneNumber))
                .role(Role.ROLE_ADMIN)
                .build();
        String refreshToken = "refresh";
        LocalDateTime refreshTokenExp = LocalDateTime.of(1970, 1, 1, 0, 0);

        user.updateRefreshTokenAndTokenExp(refreshToken, refreshTokenExp);
        userJpaRepository.save(user);

        //when
        assertThatThrownBy(() -> userService.reissueByRefreshToken(refreshToken))
                //then
                .isInstanceOf(JwtAuthenticationException.class)
                .hasMessage("해당 refresh token은 만료되었습니다.");
    }

    @DisplayName("로그아웃을 할 경우 refresh token이 비워지며, 유효기간이 현재로 설명된다")
    @Test
    void when_logout_should_returnAuthenticationToken() {
        //given
        User savedUser = saveDummyUser();
        String email = savedUser.getEmail().getValue();

        //when
        userService.logout(email);

        //then
        User logoutUser = userRepository.getByEmail(email);
        assertThat(logoutUser.getRefreshToken()).isEmpty();
        assertThat(logoutUser.getRefreshTokenExp()).isEqualTo(fixedTime);
    }

    @DisplayName("등록되지 않은 이메일로 로그아웃할 경우 예외가 발생한다")
    @Test
    void when_logoutWithUnregisteredEmail_should_throwEx() {
        //given
        String unregisteredEmail = "unregisteredEmail";

        //when
        assertThatThrownBy(() -> userService.logout(unregisteredEmail))
                //then
                .isInstanceOf(ResourceNotFoundException.class)
        ;
    }

    private User saveDummyUser() {
        JoinRequest request = new JoinRequest(dummyEmail, dummyPassword, dummyName, dummyPhoneNumber);
        return userService.save(request);
    }

    private static void refreshTokenShouldUpdated(TokenDto tokenDto, User savedUser) {
        assertThat(tokenDto.getRefreshToken()).isNotEqualTo(savedUser.getRefreshToken());
        assertThat(DateConverter.convertToLocalDateTime(
                tokenDto.getRefreshTokenExp())).isNotEqualTo(savedUser.getRefreshTokenExp());
    }

    private void tokenDtoShouldHaveValidUserInfo(TokenDto tokenDto, User savedUser) {
        assertThat(tokenDto.getUsername()).isNotEqualTo(savedUser.getUsername());
        assertThat(tokenDto.getRole()).isEqualTo(savedUser.getRole().getLabel());
    }

    private void tokenDtoShouldHaveValidAudience(TokenDto tokenDto, User savedUser) {
        String expectedAudience = savedUser.getEmail() + SecurityConst.PRINCIPAL_DELIMITER + savedUser.getName();
        assertThat(tokenManager.getTokenClaims(tokenDto.getAccessToken()).getAudience())
                .isEqualTo(expectedAudience);
        assertThat(tokenManager.getTokenClaims(tokenDto.getRefreshToken()).getAudience())
                .isEqualTo(expectedAudience);
    }
}
