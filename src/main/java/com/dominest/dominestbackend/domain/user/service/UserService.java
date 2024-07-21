package com.dominest.dominestbackend.domain.user.service;


import com.dominest.dominestbackend.api.user.request.JoinRequest;
import com.dominest.dominestbackend.domain.common.vo.PhoneNumber;
import com.dominest.dominestbackend.domain.jwt.dto.TokenDto;
import com.dominest.dominestbackend.domain.jwt.service.TokenManager;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.component.Role;
import com.dominest.dominestbackend.domain.common.vo.Email;
import com.dominest.dominestbackend.domain.user.repository.UserRepository;
import com.dominest.dominestbackend.global.config.security.SecurityConst;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.business.BusinessException;

import com.dominest.dominestbackend.global.util.ClockHolder;
import com.dominest.dominestbackend.global.util.DateConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenManager tokenManager;
    private final ClockHolder clockHolder;

    @Transactional
    public User save(JoinRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Email email = new Email(request.getEmail());
        PhoneNumber phoneNumber = new PhoneNumber(request.getPhoneNumber());

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .name(request.getName())
                .phoneNumber(phoneNumber)
                .role(Role.ROLE_ADMIN) //  현재 모든 가입자는 관리자로 고정됨.
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public TokenDto login(String email, String rawPassword) {
        // loadUserByUsername() 을 사용하지 않는다.
        User user = userRepository.getByEmail(email);

        boolean isPasswordNotMatched = !passwordEncoder.matches(rawPassword, user.getPassword());
        if (isPasswordNotMatched) {
            throw new BusinessException(ErrorCode.MISMATCHED_SIGNIN_INFO);
        }
        // audience 는 email + ":" + name 으로 구성
        String audience = user.getEmail() + SecurityConst.PRINCIPAL_DELIMITER + user.getName();

        TokenDto tokenDto = tokenManager.createTokenDto(audience);
        LocalDateTime refreshTokenExp = DateConverter.convertToLocalDateTime(tokenDto.getRefreshTokenExp());
        user.updateRefreshTokenAndTokenExp(tokenDto.getRefreshToken(), refreshTokenExp);

        tokenDto.setUsername(user.getName());
        tokenDto.setRole(user.getRole().getLabel());
        return tokenDto;
    }

    @Transactional
    // 테스트용 14일 유효기간 토큰 발급
    public TokenDto loginTemp(String email, String rawPassword) {
        // loadUserByUsername() 을 사용하지 않는다.
        User user = userRepository.getByEmail(email);

        boolean isPasswordNotMatched = !passwordEncoder.matches(rawPassword, user.getPassword());
        if (isPasswordNotMatched) {
            throw new BusinessException(ErrorCode.MISMATCHED_SIGNIN_INFO);
        }
        // audience 는 email + ":" + name 으로 구성
        String audience = user.getEmail() + SecurityConst.PRINCIPAL_DELIMITER + user.getName();

        TokenDto tokenDto = tokenManager.createTokenDtoTemp(audience, new Date(System.currentTimeMillis() + 4896000000L));
        LocalDateTime refreshTokenExp = DateConverter.convertToLocalDateTime(tokenDto.getRefreshTokenExp());
        user.updateRefreshTokenAndTokenExp(tokenDto.getRefreshToken(), refreshTokenExp);

        tokenDto.setUsername(user.getName());
        tokenDto.setRole(user.getRole().getLabel());
        return tokenDto;
    }

    @Transactional
    public TokenDto reissueByRefreshToken(String refreshToken) {
        // Member 객체를 찾아온 후 토큰 검증
        User user = userRepository.getByRefreshToken(refreshToken); // 여기서 토큰 유효성과 토큰타입(refresh) 가 검증된다.
        user.validateRefreshTokenExp(LocalDateTime.now());

        // audience 는 email + ":" + name 으로 구성
        String audience = user.getEmail() + SecurityConst.PRINCIPAL_DELIMITER + user.getName();

        TokenDto tokenDto = tokenManager.createTokenDto(audience);
        LocalDateTime refreshTokenExp = DateConverter.convertToLocalDateTime(tokenDto.getRefreshTokenExp());
        user.updateRefreshTokenAndTokenExp(tokenDto.getRefreshToken(), refreshTokenExp);

        tokenDto.setUsername(user.getName());
        tokenDto.setRole(user.getRole().getLabel());
        return tokenDto;
    }

    @Transactional
    public void logout(String email) {
        User user = userRepository.getByEmail(email);
        user.logout(clockHolder.now());
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.getByEmail(email);

        if (isPasswordNotMatched(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_CODE_MISMATCHED);
        }
        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private boolean isPasswordNotMatched(String currentPassword, String loggedInUserPassword) {
        return passwordEncoder.matches(currentPassword, loggedInUserPassword);
    }
}
