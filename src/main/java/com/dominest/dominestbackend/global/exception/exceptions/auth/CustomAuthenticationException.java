package com.dominest.dominestbackend.global.exception.exceptions.auth;

import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.CustomException;

public class CustomAuthenticationException extends CustomException {

    public CustomAuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CustomAuthenticationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
