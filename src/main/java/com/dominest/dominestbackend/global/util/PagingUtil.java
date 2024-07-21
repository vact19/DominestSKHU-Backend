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
public class PagingUtil {
    private static final String ERROR_MSG_PAGE_LESS_THEN_ONE = "page는 1 이상이어야 합니다.";

    /**
     * @param oneBasedPage 1-based page
     * @return 0-based pageable Instance
     */
    public static Pageable getPageable(int oneBasedPage, int size) {
        if (oneBasedPage < 1)
            throw new IllegalArgumentException(ERROR_MSG_PAGE_LESS_THEN_ONE);

        return PageRequest.of(oneBasedPage - 1, size);
    }

    /**
     * @param oneBasedPage 1-based page
     * @return 0-based pageable Instance
     */
    public static Pageable getPageable(int oneBasedPage, int size, Sort sort) {
        if (oneBasedPage < 1)
            throw new IllegalArgumentException(ERROR_MSG_PAGE_LESS_THEN_ONE);

        return PageRequest.of(oneBasedPage - 1, size, sort);
    }

    /**
     * offset 기반 페이지네이션 구현 시
     * 1페이지 조회시에 모든 데이터를 count 하지 않기 위해
     * (기본 페이지 사이즈 * 화면에 표시할 페이지 수) + 1 만큼 count 하도록 LIMIT 를 설정한다.
     * <p>
     * pageDisplayLimit 10
     * defaultPageSize 20일 때
     * 1~10페이지 조회시에는 201
     * 11~20페이지 조회시에는 401
     * 100~110페이지 조회시에는 2001을 LIMIT 으로 함.
     * 현재 페이지가 전체에서 몇 페이지냐를 알아야 하기 때문에 OFFSET 은 고정.
     *
     * @param page 조회 대상 페이지
     * @param pageDisplayLimit 한 화면에 표시 가능한 페이지의
     * @return count query 에 사용할 limit
     */
    public static int getPageGroupLimit(int page, int pageDisplayLimit, int defaultPageSize) {
        return ((page - 1) / pageDisplayLimit + 1) * pageDisplayLimit * defaultPageSize + 1;
    }
}
