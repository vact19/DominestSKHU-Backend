package com.dominest.dominestbackend.global.exception.exceptions.domain;

import com.dominest.dominestbackend.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 비즈니스 로직 상 예외
@Getter
public class DomainException extends RuntimeException {
    private final int statusCode;
    private final HttpStatus httpStatus;

    public DomainException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.statusCode = errorCode.getStatusCode();
        this.httpStatus = HttpStatus.valueOf(errorCode.getStatusCode());
    }

    public DomainException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.statusCode = errorCode.getStatusCode();
        this.httpStatus = HttpStatus.valueOf(errorCode.getStatusCode());
    }

    public DomainException(String message, HttpStatus httpStatus) {
        super(message);
        this.statusCode = httpStatus.value();
        this.httpStatus = httpStatus;
    }

    public DomainException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.statusCode = httpStatus.value();
        this.httpStatus = httpStatus;
    }
}