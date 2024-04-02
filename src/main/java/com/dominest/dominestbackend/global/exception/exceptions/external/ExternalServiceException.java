package com.dominest.dominestbackend.global.exception.exceptions.external;

import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.CustomException;
import org.springframework.http.HttpStatus;

// DB, 외부 API 예외처럼 핵심 도메인 로직을 제외한 외부 서비스 예외에 사용
public class ExternalServiceException extends CustomException {

    public ExternalServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ExternalServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ExternalServiceException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
