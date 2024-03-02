package com.dominest.dominestbackend.domain.common;

import lombok.RequiredArgsConstructor;

/**
 * DB에서 찾을 수 있는 데이터들의 목록. ResourceNotFoundException에서 사용된다.
 */
@RequiredArgsConstructor
public enum Datasource {
    ROOM("방")
    , USER("사용자")
    , FAVORITE("즐겨찾기")
    , CARD_KEY("카드키")
    , CATEGORY("카테고리")
    , COMPLAINT("민원내역")
    , IMAGE_TYPE("이미지 게시글")
    , SANITATION_CHECK_POST("방역점검 게시글")
    , FLOOR("방역점검층")
    , CHECKED_ROOM("방역점검호실")
    , UNDELIVERED_PARCEL_POST("장기 미수령 택배 게시글")
    , UNDELIVERED_PARCEL("장기 미수령 택배")
    , MANUAL_POST("공지사항")
    , RESIDENT("입사자")
    , SCHEDULE("일정")
    , TODO("할 일")

    ;
    public final String label;
}
