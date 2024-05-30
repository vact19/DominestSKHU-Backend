package com.dominest.dominestbackend.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

class DateConverterTest {

    @DisplayName("Date를 LocalDateTime으로 변환한다.")
    @Test
    void convertDateToLocalDate() {
        //given
        final int YEAR = 2000; final int MONTH = 1; final int DAY_OF_MONTH = 1;
        final int HOUR = 0; final int MINUTE = 0; final int SECOND = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.set(YEAR, Calendar.JANUARY, DAY_OF_MONTH, HOUR, MINUTE, SECOND);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();

        LocalDateTime localDateTime = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR, MINUTE, SECOND);
        //when
        LocalDateTime result = DateConverter.convertToLocalDateTime(date);
        //then
        assertThat(result).isEqualTo(localDateTime);
    }
}
