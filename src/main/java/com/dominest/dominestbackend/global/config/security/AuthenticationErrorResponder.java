package com.dominest.dominestbackend.global.config.security;

import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.dto.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class AuthenticationErrorResponder {
    private final ObjectMapper objectMapper;
    public void respond(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorCode errorCode = (ErrorCode) request.getAttribute(ErrorCode.class.getSimpleName());
        HttpStatus httpStatus = HttpStatus.valueOf(errorCode.getStatusCode());

        ErrorResponseDto<String> errDto = new ErrorResponseDto<>(
                httpStatus
                , errorCode.getMessage());
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), errDto);
    }
}
