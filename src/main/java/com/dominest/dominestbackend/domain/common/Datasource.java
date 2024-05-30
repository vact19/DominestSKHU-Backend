package com.dominest.dominestbackend.domain.common;

import lombok.RequiredArgsConstructor;

/**
 * DB에서 찾을 수 있는 데이터들의 목록. ResourceNotFoundException에서 사용된다. @see com.dominest.dominestbackend.global.exception.exceptions.external.common.ResourceNotFoundException
 * 각 테이블의 한글 용어사전으로써의 기능도 함.
 */
@RequiredArgsConstructor
public enum Datasource {

    // ======== 사용자 ========
    // 사용자(근로생, 직원)
    USER("사용자")
    // 근무시간표
    , SCHEDULE("근무시간표")
    , DAY_OF_WEEK("요일")
    , TIME_SLOT("시간대")
    // 일정표
    , CALENDAR("일정")
    // 할 일
    , TODO("할 일")
    // 알림
    , DATE_NOTICE("일정 알림")
    , REPEAT_SCHEDULE("반복 알림 게시글")
    , REPEAT_NOTICE("반복 알림")
    // ======== END ========

    // 게시글
    , FAVORITE("즐겨찾기")
    , CARD_KEY("카드키")
    , CATEGORY("카테고리")
    , COMPLAINT("민원내역")
    , IMAGE_TYPE("이미지 게시글")
    , INSPECTION_POST("방역점검 게시글")
    , INSPECTION_FLOOR("방역점검층")
    , INSPECTION_ROOM("방역점검호실")
    , UNDELIVERED_PARCEL_POST("장기 미수령 택배 게시글")
    , UNDELIVERED_PARCEL("장기 미수령 택배")
    , MANUAL_POST("공지사항")

    // 입사생
    , RESIDENT("입사자")
    , ROOM("방")

    ;
    public final String label;
}
