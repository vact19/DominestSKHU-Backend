package com.dominest.dominestbackend.global.exception.exceptions.auth.security;

import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.auth.CustomAuthenticationException;

public class AnonymousUserException extends CustomAuthenticationException {
    public AnonymousUserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AnonymousUserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
