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
     * 'yy'의 파싱 결과는 기본적으로 2000년대이지만, {@code baseYear}를 넘지 않도록 설정된다.
     * <p>
     * 생년을 해석함에 있어 [90년생과 00년생]을 각각 [1990, 2000년생]으로 해석하기 위함.
     * <br>
     * baseYear가 2020일 때, <br>
     * ex) 200101 -> 2020-01-01 <br>
     * ex) 210101 -> 1921-01-01 <br>
     * ex) 990101 -> 1999-01-01
     *
     * @param yyMMdd 변환할 날짜에 해당하는 문자열. ex) 990401
     * @param baseYear 기준 연도. ex) 2024
     * @return 1999-04-01 LocalDate
     * @throws IllegalArgumentException 인자가 유효한 yyMMdd 형식이 아니어서 LocalDate로 파싱할 수 없는 경우
     */
    public static LocalDate parseyyMMddToLocalDate(int baseYear, String yyMMdd) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(yyMMdd, DateTimeFormatter.ofPattern("yyMMdd"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("인자가 유효한 yyMMdd 형식이 아니므로 날짜 타입으로 파싱할 수 없습니다.");
        }

        int year = localDate.getYear();
        // 현재 연도보다 미래인 경우 100년 전으로 설정
        if (year > baseYear) {
            localDate = localDate.minusYears(100);
        }
        return localDate;
    }
}
