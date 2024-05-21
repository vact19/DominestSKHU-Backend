package com.dominest.dominestbackend.global.exception.handler;

import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.dto.ErrorResponseDto;
import com.dominest.dominestbackend.global.exception.exceptions.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * \@ModelAttribute 으로 binding error 발생시 BindException 발생한다.
     * \@RequestBody @Valid 바인딩 오류(HttpMessageConverter binding)시 ConstraintViolationException 을 추상화한
     *  MethodArgumentNotValidException 도 BindException 을 확장한다.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDto<Map<String, String>>> handleBindException(BindException e, HttpServletRequest request) {
        printLog(e, request);

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        StringBuilder sb = new StringBuilder();
        Map<String, String> errorInfoMap = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            String errorMsg = sb
                    .append(fieldError.getDefaultMessage())
                    .append(" - 요청받은 값: ")
                    .append(fieldError.getRejectedValue())
                    .toString();

            errorInfoMap.put(fieldError.getField(), errorMsg);

            sb.setLength(0);
        }

        return createErrorResponse(HttpStatus.BAD_REQUEST, errorInfoMap);
    }

    /** spring handler @RequestParam 파라미터 누락*/
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        printLog(e, request);
        String message = "파라미터 '" + e.getParameterName() + "'이 누락되었습니다.";
        return createErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDto<String>> handleBusinessException(IllegalArgumentException e, HttpServletRequest request){
        printLog(e, request);
        return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // ENUM 변환실패, 날짜타입에 2999-15-99 와 같은 잘못된 값이 들어올 때 발생
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidFormatException(HttpMessageNotReadableException e, HttpServletRequest request){
        printLog(e, request);
        return createErrorResponse(ErrorCode.HTTP_MESSAGE_NOT_READABLE);
    }

    // global.exception.exceptions 패키지의 커스텀 예외 처리를 담당한다.
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleBusinessException(CustomException e, HttpServletRequest request){
        printLog(e, request);
        return createErrorResponse(e.getHttpStatus(), e.getMessage());
    }

    // 예상하지 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto<String>> handleException(Exception e, HttpServletRequest request){
        log.error("예외처리 범위 외의 오류 발생.");
        printLog(e, request);

        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private <T> ResponseEntity<ErrorResponseDto<T>> createErrorResponse(HttpStatus httpStatus, T errorMessage) {
        ErrorResponseDto<T> errDto = new ErrorResponseDto<>(httpStatus, errorMessage);
        return ResponseEntity.status(httpStatus).body(errDto);
    }

    private ResponseEntity<ErrorResponseDto<String>> createErrorResponse(ErrorCode errorCode) {
        HttpStatus httpStatus = HttpStatus.valueOf(errorCode.getStatusCode());

        ErrorResponseDto<String> errDto = new ErrorResponseDto<>(
                httpStatus
                , errorCode.getMessage());
        return ResponseEntity.status(httpStatus).body(errDto);
    }

    private void printLog(Exception e, HttpServletRequest request) {
        log.error("발생 예외: {}, 에러 메시지: {}, 요청 Method: {}, 요청 url: {}",
                e.getClass().getSimpleName(), e.getMessage(), request.getMethod(), request.getRequestURI(), e);
    }
}
