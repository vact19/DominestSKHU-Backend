package com.dominest.dominestbackend.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Jackson 파싱
    HTTP_MESSAGE_NOT_READABLE(400, "요청값을 읽어들이지 못했습니다. 형식을 확인해 주세요."),

    // 이메일
    EMAIL_VERIFICATION_CODE_MISMATCHED(400, "이메일 인증 코드가 일치하지 않습니다."),
    EMAIL_ALREADY_REGISTERED(409, "이미 가입된 이메일입니다."),
    EMAIL_CANNOT_BE_SENT(500, "이메일을 보낼 수 없습니다."),
    EMAIL_VERIFICATION_CODE_NOT_FOUND(400, "이메일 인증 코드가 존재하지 않습니다."),
    EMAIL_CANNOT_BE_CREATED(500, "이메일을 생성할 수 없습니다."),

    // 인증 - 로그인 시도
    MISMATCHED_SIGNIN_INFO(400, "잘못된 로그인 정보입니다."),

    // 인증 - 토큰
    NOT_EXISTS_AUTH_HEADER(401, "Authorization Header가 빈 값입니다."),
    NOT_VALID_BEARER_GRANT_TYPE(401, "인증 타입이 Bearer 타입이 아닙니다."),
    TOKEN_EXPIRED(401, "해당 token은 만료되었습니다."),
    ACCESS_TOKEN_EXPIRED(401, "해당 access token은 만료되었습니다."),
    NOT_ACCESS_TOKEN_TYPE(401, "tokenType이 access token이 아닙니다."),
    REFRESH_TOKEN_EXPIRED(401, "해당 refresh token은 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(400, "해당 refresh token은 존재하지 않습니다."),
    NOT_VALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    NO_ACCESS_USER(403, "권한이 없습니다."),

    // 인증 - Spring Security
    ANONYMOUS_USER(401, "인증되지 않은 사용자입니다."),

    // 엑셀 업로드
    COLUMN_COUNT_MISMATCHED(400, "컬럼의 개수가 일치하지 않습니다."),
    INVALID_FILE_EXTENSION(400, "파일 확장자가 유효하지 않습니다."),
    DUPLICATED_RESIDENT_ON_UPLOAD(400, "엑셀 데이터 중 중복된 사생 데이터가 존재합니다."),

    // 파일
    FILE_CANNOT_BE_STORED(500, "파일을 저장할 수 없습니다."),
    FILE_CANNOT_BE_READ(500, "파일을 읽을 수 없습니다."),
    FILE_CANNOT_BE_SENT(500, "읽어들인 파일을 전송할 수 없습니다"),
    UNSAVED_FILE_EXISTS(500, "저장되지 않은 파일이 존재합니다."),
    MULTIPART_FILE_CANNOT_BE_READ(500, "업로드한 파일을 읽을 수 없습니다."),
    NO_FILE_UPLOADED(400, "파일명과 현재 입사자 이름이 일치하지 않아 파일이 업로드되지 않았습니다."),
    FILE_NOT_FOUND(400, "해당 파일이 존재하지 않습니다."),
    FILE_CANNOT_BE_DELETED(500, "파일을 삭제할 수 없습니다."),
    FOLDER_CANNOT_BE_DELETED(500, "재귀적으로 파일을 삭제하는 과정에서 오류가 발생했습니다."),
    FOLDER_CANNOT_BE_CREATED(500, "폴더를 생성하는 과정에서 오류가 발생했습니다."),

    // 카테고리
    CATEGORY_TYPE_MISMATCHED(400, "해당 카테고리의 타입이 일치하지 않습니다."),
    CATEGORY_ORDER_KEY_DUPLICATED(400, "카테고리의 orderKey가 중복됩니다."),
    CANNOT_DELETE_ASSOCIATED_POST(409, "해당 카테고리에 연관된 게시글이 존재합니다."),

    // 투두
    TODO_NOT_FOUND(404, "해당 투두가 존재하지 않습니다."),

    // 스케줄
    USER_NOT_FOUND_IN_SCHEDULE(404, "해당 시간대에 저장된 유저가 존재하지 않습니다."),

    // 알림
    NOT_CORRECT_DAY(400, "요일 정보가 잘못되었습니다. 1(월요일)에서 7(일요일) 사이의 숫자를 입력해주세요.")
    ;

    private final int statusCode;
    private final String message;

    ErrorCode(int status, String message) {
        this.statusCode = status;
        this.message = message;
    }
}
