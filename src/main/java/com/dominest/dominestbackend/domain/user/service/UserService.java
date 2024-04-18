package com.dominest.dominestbackend.domain.user.service;


import com.dominest.dominestbackend.api.user.request.JoinRequest;
import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.jwt.dto.TokenDto;
import com.dominest.dominestbackend.domain.jwt.service.TokenManager;
import com.dominest.dominestbackend.domain.user.User;
import com.dominest.dominestbackend.domain.user.component.Role;
import com.dominest.dominestbackend.domain.user.repository.UserRepository;
import com.dominest.dominestbackend.global.config.security.SecurityConst;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.business.BusinessException;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenManager tokenManager;

    @Transactional
    public void create(JoinRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.ROLE_ADMIN) //  현재 모든 가입자는 관리자로 고정됨.
                .build();

        userRepository.save(user);
    }

    @Transactional
    public TokenDto login(String email, String rawPassword) {
        // loadUserByUsername() 을 사용하지 않는다.
        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.MISMATCHED_SIGNIN_INFO);
        }
        // audience 는 email + ":" + name 으로 구성
        String audience = user.getEmail() + SecurityConst.PRINCIPAL_DELIMITER + user.getName();

        TokenDto tokenDto = tokenManager.createTokenDto(audience);
        // refresh token은 관리를 위해 user DB에 저장.
        user.updateRefreshTokenAndExp(tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExp());

        tokenDto.setUsername(user.getName());
        tokenDto.setRole(user.getRole().getLabel());
        return tokenDto;
    }

    @Transactional
    // 테스트용 14일 유효기간 토큰 발급
    public TokenDto loginTemp(String email, String rawPassword) {
        // loadUserByUsername() 을 사용하지 않는다.
        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.MISMATCHED_SIGNIN_INFO);
        }
        // audience 는 email + ":" + name 으로 구성
        String audience = user.getEmail() + SecurityConst.PRINCIPAL_DELIMITER + user.getName();

        TokenDto tokenDto = tokenManager.createTokenDtoTemp(audience, new Date(System.currentTimeMillis() + 4896000000L));
        // refresh token은 관리를 위해 user DB에 저장.
        user.updateRefreshTokenAndExp(tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExp());

        tokenDto.setUsername(user.getName());
        tokenDto.setRole(user.getRole().getLabel());
        return tokenDto;
    }

    @Transactional
    public TokenDto reissueByRefreshToken(String refreshToken) {
        // Member 객체를 찾아온 후 토큰 검증
        User user = findByRefreshToken(refreshToken); // 여기서 토큰 유효성과 토큰타입(refresh) 가 검증된다.
        user.validateRefreshTokenExp();

        // audience 는 email + ":" + name 으로 구성
        String audience = user.getEmail() + SecurityConst.PRINCIPAL_DELIMITER + user.getName();

        TokenDto tokenDto = tokenManager.createTokenDto(audience);
        user.updateRefreshTokenAndExp(tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExp());

        tokenDto.setUsername(user.getName());
        tokenDto.setRole(user.getRole().getLabel());
        return tokenDto;
    }

    private User findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.USER
                        , "refreshToken", refreshToken));
    }

    public boolean validateUserPassword(String currentPassword, String loggedInUserPassword) {
        return passwordEncoder.matches(currentPassword, loggedInUserPassword);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.USER, "email", email));
    }

    @Transactional
    public void logout(String email) {
        User user = getUserByEmail(email);
        user.logout();
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = getUserByEmail(email);

        if (validateUserPassword(oldPassword, user.getPassword())) {
            user.changePassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_CODE_MISMATCHED);
        }
    }
}
