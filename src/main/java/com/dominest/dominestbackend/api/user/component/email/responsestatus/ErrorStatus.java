package com.dominest.dominestbackend.api.user.component.email.responsestatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorStatus {

    /* BAD_REQUEST */
    VERIFY_EMAIL_FAILED(HttpStatus.BAD_REQUEST, "이메일 인증 실패"),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
