package com.dominest.dominestbackend.api.user.component.email.responsestatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessStatus {
    SEND_EMAIL_SUCCESS(HttpStatus.OK, "이메일 전송 성공!"),
    VERIFY_EMAIL_SUCCESS(HttpStatus.OK, "이메일 인증 성공!");

    private final HttpStatus httpStatus;
    private final String message;
}
