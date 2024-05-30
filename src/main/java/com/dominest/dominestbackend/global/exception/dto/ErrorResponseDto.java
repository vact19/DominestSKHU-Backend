package com.dominest.dominestbackend.global.exception.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponseDto<T> {
    private final int statusCode;
    private final String httpStatus;
    private final T errorMessage;

    public ErrorResponseDto(HttpStatus httpStatus, T errorMessage) {
        this.statusCode = httpStatus.value();
        this.httpStatus = httpStatus.getReasonPhrase();
        this.errorMessage = errorMessage;
    }
}
