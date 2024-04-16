package com.dominest.dominestbackend.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.*;

class PageBaseConverterTest {

    @DisplayName("1-based page를 0-based pageable로 변환한다")
    @Test
    void of() {
        //given
        int size = 10;
        int oneBasedPage = 2;
        int zeroBasedPage = oneBasedPage - 1;

        Pageable pageable = PageRequest.of(zeroBasedPage, size);

        //when
        Pageable pageableFromConverter = PageBaseConverter.of(oneBasedPage, size);
        //then
        assertThat(pageableFromConverter).isEqualTo(pageable);
    }

    @DisplayName("1보다 작은 페이지가 들어오면 예외를 던진다")
    @Test
    void of_whenOneBasedPageIsLessThanOne() {
        //given
        int size = 10;
        int oneBasedPage = 0;

        //when, then
        assertThatThrownBy(() -> PageBaseConverter.of(oneBasedPage, size))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
