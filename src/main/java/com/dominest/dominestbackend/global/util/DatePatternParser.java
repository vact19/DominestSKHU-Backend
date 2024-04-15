package com.dominest.dominestbackend.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatePatternParser {
    /**
     * yyMMdd 형식의 문자열을 LocalDate로 변환한다.
     * 'yy' 가 메서드 실행 시점의 연도보다 미래인 경우 100년 전으로 설정한다.
     * 생년을 해석함에 있어 [90년생과 00년생]을 각각 [1990, 2000년생]으로 해석하기 위함.
     *
     * @param yyMMdd ex) 990401
     * @return 1999-04-01 LocalDate
     */
    public static LocalDate parseyyMMddToLocalDate(String yyMMdd) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(yyMMdd, DateTimeFormatter.ofPattern("yyMMdd"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("인자가 yyMMdd 형식이 아니므로 날짜 타입으로 파싱할 수 없습니다.");
        }

        int year = localDate.getYear();
        // 현재 연도보다 미래인 경우 100년 전으로 설정
        if (year > LocalDate.now().getYear()) {
            localDate = localDate.minusYears(100);
        }
        return localDate;
    }
}
