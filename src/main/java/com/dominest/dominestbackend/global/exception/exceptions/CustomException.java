package com.dominest.dominestbackend.global.exception.exceptions;

import com.dominest.dominestbackend.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class CustomException extends RuntimeException {
    private final int statusCode;
    private final HttpStatus httpStatus;

    protected CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.statusCode = errorCode.getStatusCode();
        this.httpStatus = HttpStatus.valueOf(errorCode.getStatusCode());
    }

    protected CustomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.statusCode = errorCode.getStatusCode();
        this.httpStatus = HttpStatus.valueOf(errorCode.getStatusCode());
    }

    protected CustomException(String message, HttpStatus httpStatus) {
        super(message);
        this.statusCode = httpStatus.value();
        this.httpStatus = httpStatus;
    }

    protected CustomException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.statusCode = httpStatus.value();
        this.httpStatus = httpStatus;
    }
}
