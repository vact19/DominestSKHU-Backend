package com.dominest.dominestbackend.domain.post.inspection.floor.room.entity;

import com.dominest.dominestbackend.domain.common.jpa.BaseEntity;
import com.dominest.dominestbackend.domain.post.inspection.entity.InspectionPost;
import com.dominest.dominestbackend.domain.post.inspection.floor.entity.InspectionFloor;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.component.InspectionResidentInfo;
import com.dominest.dominestbackend.domain.room.entity.Room;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import javax.persistence.*;

/**
 * 방역점검 대상 방의 결과를 저장한다.
 * 참조 관계는 {@link InspectionPost} <- {@link InspectionFloor} <- this (즉, 게시글 <- 글에 속한 층 <- 층에 속한 점검대상 방)
 * <br><br>
 * 아래의 7가지 사항들을 점검한다.
 * <p>
 * 1. 실내  2. 쓰레기방치  3. 화장실
 * 4. 샤워실 5. 보관금지
 * <br>
 * 6. 통과(ENUM) 7. 비고 (String 자유입력)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class InspectionRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean indoor = false;
    private boolean leavedTrash = false;
    private boolean toilet = false;
    private boolean shower = false;
    private boolean prohibitedItem = false;

    @Enumerated(EnumType.STRING)
    private PassState passState;
    private String etc;

    @Embedded
    private InspectionResidentInfo inspectionResidentInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private InspectionFloor inspectionFloor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Builder
    private InspectionRoom(PassState passState, String etc, InspectionResidentInfo inspectionResidentInfo
            , Room room, InspectionFloor inspectionFloor) {
        // ==기본값 설정==
        this.indoor = false;
        this.leavedTrash = false;
        this.toilet = false;
        this.shower = false;
        this.prohibitedItem = false;

        this.inspectionResidentInfo = inspectionResidentInfo;
        this.passState = passState;
        this.etc = etc;

        this.room = room;
        this.inspectionFloor = inspectionFloor;
    }

    public void updateValuesOnlyNotNull(Boolean indoor, Boolean leavedTrash
            , Boolean toilet, Boolean shower, Boolean prohibitedItem
            , PassState passState, String etc) {
        if (indoor != null) {this.indoor = indoor;}
        if (leavedTrash != null) {this.leavedTrash = leavedTrash;}
        if (toilet != null) {this.toilet = toilet;}
        if (shower != null) {this.shower = shower;}
        if (prohibitedItem != null) {this.prohibitedItem = prohibitedItem;}
        if (passState != null) {this.passState = passState;}
        if (etc != null) {this.etc = etc;}
    }

    public void passAll() {
        this.indoor = true;
        this.leavedTrash = true;
        this.toilet = true;
        this.shower = true;
        this.prohibitedItem = true;
    }

    @Getter
    @RequiredArgsConstructor
    public enum PassState {
        NOT_PASSED("미통과", 0), FIRST_PASSED("1차 통과", 0)
        , SECOND_PASSED("2차 통과", 3), THIRD_PASSED("3차 통과", 6)
        , FOURTH_PASSED("4차 통과", 9), FIFTH_PASSED("5차 통과", 12)
        , SIXTH_PASSED("6차 통과", 15), SEVENTH_PASSED("7차 통과", 18)
        , EIGHTH_PASSED("8차 통과", 21), NINTH_PASSED("9차 통과", 24)
        , TENTH_PASSED("10차 통과", 27)
        ;

        @JsonValue
        private final String label;
        private final int penalty;
    }
}
