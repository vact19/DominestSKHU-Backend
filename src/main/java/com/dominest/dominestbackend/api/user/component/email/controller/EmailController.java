package com.dominest.dominestbackend.api.user.component.email.controller;

import com.dominest.dominestbackend.api.common.ResponseTemplate;
import com.dominest.dominestbackend.api.user.component.email.request.EmailRequest;
import com.dominest.dominestbackend.domain.user.component.email.service.EmailService;
import com.dominest.dominestbackend.domain.user.component.email.service.EmailVerificationService;
import com.dominest.dominestbackend.api.user.component.email.responsestatus.ErrorStatus;
import com.dominest.dominestbackend.api.user.component.email.responsestatus.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send") // 인증번호 발송 버튼 누르면 메일 가게
    public ResponseEntity<ResponseTemplate<String>> sendEmail(@RequestBody EmailRequest emailRequest) {
        emailService.sendJoinMessage(emailRequest.getValue()); // 이메일로 인증코드를 보낸다.

        SuccessStatus successStatus = SuccessStatus.SEND_EMAIL_SUCCESS;
        ResponseTemplate<String> result = new ResponseTemplate<>(successStatus.getHttpStatus(), successStatus.getMessage()
                , emailRequest.getValue() + "로 검증코드를 전송했습니다.");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/change/password") // 임시 비밀번호 이메일 전송
    public ResponseEntity<ResponseTemplate<String>> changePasswordEmail(@RequestBody EmailRequest emailRequest) {
        emailService.sendChangeMessage(emailRequest.getValue()); // 이메일로 인증코드를 보냄

        SuccessStatus successStatus = SuccessStatus.SEND_EMAIL_SUCCESS;
        ResponseTemplate<String> result = new ResponseTemplate<>(successStatus.getHttpStatus(), successStatus.getMessage(), emailRequest.getValue() + "로 검증코드를 전송했습니다.");
        return ResponseEntity.ok(result);
    }


    @PostMapping("/verify/code") // 이메일 인증코드 검증
    public ResponseEntity<ResponseTemplate<String>> verifyEmail(@RequestBody EmailRequest emailRequest) {
        boolean success = emailVerificationService.verifyCode(emailRequest.getValue(), emailRequest.getCode());
        if (success) { // 인증 성공
            SuccessStatus successStatus = SuccessStatus.VERIFY_EMAIL_SUCCESS;
            ResponseTemplate<String> result = new ResponseTemplate<>(successStatus.getHttpStatus(), successStatus.getMessage());
            return ResponseEntity.ok(result); // 이메일과 함께 성공 응답 반환
        } else { // 인증 실패
            ErrorStatus errorStatus = ErrorStatus.VERIFY_EMAIL_FAILED;
            ResponseTemplate<String> result = new ResponseTemplate<>(errorStatus.getHttpStatus(), errorStatus.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 Bad Req 상태로 실패 응답 반환
                    .body(result);
        }
    }
}
