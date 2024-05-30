package com.dominest.dominestbackend.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class DatePatternParserTest {

    @DisplayName("yyMMdd 형식의 문자열을 LocalDate로 변환한다")
    @Test
    void parse_yyMMddToLocalDate() {
        // given
        int baseYear = 2020;
        String yyMMdd = "981231";

        // when
        LocalDate result = DatePatternParser.parseyyMMddToLocalDate(baseYear, yyMMdd);

        // then
        assertThat(result).isEqualTo(LocalDate.of(1998, 12, 31));
    }

    @DisplayName("파싱 결과가 기준연도를 넘는 경우 1900년대로 해석한다")
    @Test
    void parse_yyMMddToLocalDate_whenExceedBaseYear() {
        // given
        int baseYear = 2020;
        String yyMMdd = "210101";

        // when
        LocalDate result = DatePatternParser.parseyyMMddToLocalDate(baseYear, yyMMdd);

        // then
        assertThat(result).isEqualTo(LocalDate.of(1921, 1, 1));
    }

    @DisplayName("파싱 결과가 기준연도를 넘지 않는 경우 2000년대로 해석한다")
    @Test
    void parse_yyMMddToLocalDate_whenNotExceedBaseYear() {
        // given
        int baseYear = 2020;
        String yyMMdd = "200101";

        // when
        LocalDate result = DatePatternParser.parseyyMMddToLocalDate(baseYear, yyMMdd);

        // then
        assertThat(result).isEqualTo(LocalDate.of(2020, 1, 1));
    }

    @DisplayName("파라미터의 패턴이 yyMMdd 형식이 아닐 경우 예외 발생")
    @Test
    void parse_yyMMddToLocalDate_whenInvalidPattern() {
        // given
        int baseYear = 2020;
        String invalidPattern = "INVALID_PATTERN";

        // when
        assertThatThrownBy(() -> {
            DatePatternParser.parseyyMMddToLocalDate(baseYear, invalidPattern);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("파라미터의 패턴이 정상 날짜범위가 아닌 경우 예외 발생")
    @Test
    void parse_yyMMddToLocalDate_whenInvalidDate() {
        // given
        int baseYear = 2020;
        String invalidMonthAndDate = "201332";

        // when
        assertThatThrownBy(() -> {
            DatePatternParser.parseyyMMddToLocalDate(baseYear, invalidMonthAndDate);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
