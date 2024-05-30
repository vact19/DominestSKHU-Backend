package com.dominest.dominestbackend.global.exception.exceptions.auth.jwt;

import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.auth.CustomAuthenticationException;

public class JwtAuthenticationException extends CustomAuthenticationException {
    public JwtAuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
    public JwtAuthenticationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
