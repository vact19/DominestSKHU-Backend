package com.dominest.dominestbackend.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 401 에러 핸들러를 구현.
@RequiredArgsConstructor
@Component
public class Custom401AuthEntryPoint implements AuthenticationEntryPoint {
    private final AuthenticationErrorResponder authenticationErrorResponder;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        authenticationErrorResponder.respond(request, response);
    }
}
