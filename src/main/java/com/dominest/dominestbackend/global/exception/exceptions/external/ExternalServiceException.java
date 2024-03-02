package com.dominest.dominestbackend.global.exception.exceptions.external;

import com.dominest.dominestbackend.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// DB, 외부 API 등 핵심 도메인 로직을 제외한 외부 I/O 예외에 사용
@Getter
public class ExternalServiceException extends RuntimeException{
    private final int statusCode;
    private final HttpStatus httpStatus;

    public ExternalServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.statusCode = errorCode.getStatusCode();
        this.httpStatus = HttpStatus.valueOf(errorCode.getStatusCode());
    }

    public ExternalServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.statusCode = errorCode.getStatusCode();
        this.httpStatus = HttpStatus.valueOf(errorCode.getStatusCode());
    }

    public ExternalServiceException(String message, HttpStatus httpStatus) {
        super(message);
        this.statusCode = httpStatus.value();
        this.httpStatus = httpStatus;
    }
}
