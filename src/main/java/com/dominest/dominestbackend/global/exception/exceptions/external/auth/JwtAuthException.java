package com.dominest.dominestbackend.global.exception.exceptions.external.auth;

import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.external.ExternalServiceException;

public class JwtAuthException extends ExternalServiceException {
    public JwtAuthException(ErrorCode errorCode) {
        super(errorCode);
    }
    public JwtAuthException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
