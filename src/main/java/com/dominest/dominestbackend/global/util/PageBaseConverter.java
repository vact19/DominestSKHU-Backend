package com.dominest.dominestbackend.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 0-base인 페이지를 클라이언트단에서 1-based인 것처럼 사용할 수 있게 한다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageBaseConverter {
    private static final String ERROR_MSG_PAGE_LESS_THEN_ONE = "page는 1 이상이어야 합니다.";
    /**
     * @param oneBasedPage 1-based page
     * @return 0-based pageable Instance
     * */
    public static Pageable of(int oneBasedPage, int size) {
        if (oneBasedPage < 1)
            throw new IllegalArgumentException(ERROR_MSG_PAGE_LESS_THEN_ONE);

        return PageRequest.of(oneBasedPage - 1 , size);
    }

    /**
     * @param oneBasedPage 1-based page
     * @return 0-based pageable Instance
     * */
    public static Pageable of(int oneBasedPage, int size, Sort sort) {
        if (oneBasedPage < 1)
            throw new IllegalArgumentException(ERROR_MSG_PAGE_LESS_THEN_ONE);

        return PageRequest.of(oneBasedPage - 1 , size, sort);
    }
}
