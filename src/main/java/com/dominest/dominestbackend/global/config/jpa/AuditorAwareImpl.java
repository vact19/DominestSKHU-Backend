package com.dominest.dominestbackend.global.config.jpa;

import com.dominest.dominestbackend.domain.jwt.service.TokenManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final HttpServletRequest httpServletRequest;
    private final TokenManager tokenManager;

    @NonNull
    @Override
    public Optional<String> getCurrentAuditor() {
        //  1. authorization 필수 체크. 헤더 부분에 Authorization 이 없으면 지정한 예외를 발생시킴
        //  토큰 유무 확인
        String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1];

        String email = tokenManager.getMemberEmail(token);
        return Optional.of(email);
    }
}
