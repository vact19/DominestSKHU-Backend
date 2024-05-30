package com.dominest.dominestbackend.domain.room.entity;

import com.dominest.dominestbackend.domain.common.jpa.BaseEntity;
import com.dominest.dominestbackend.domain.resident.entity.Resident;
import com.dominest.dominestbackend.global.exception.exceptions.business.BusinessException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 *  기숙사 호실 정보. 행기 기숙사 데이터.

 *  엑셀 데이터는 roomNo, dormitoryCode, assignedRoom 이며
 *  나머지 속성들은 개발 편의를 위하여 추가함.

 *  아래 2가지의 데이터는 잘 쓰지 않는다.
 *  1. 호실 roomNumber(1(미가엘), 2(행기))
 *  2. ,기숙사 dormitoryCode (A(미) B(행)),

 *  아래 데이터를 주로 다루게 될 듯.
 *  1. 배정방 assignedRoom
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
public class Room extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Unique 할 수밖에 없음.
    private final String assignedRoom; // 배정방. 'B1049A' 와 같음
    @Column(nullable = false)
    private final int floorNo; // 층수

    // 한 방에 한 입사생만 입주할 수 있지만, 입사생은 여러 차수로 나뉘어져 있으므로 OneToMany
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Resident> residents;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Dormitory dormitory;

    public Room(String assignedRoom, int floorNo, Dormitory dormitory) {
        if (assignedRoom == null || assignedRoom.length() != 6 ) {
            throw new BusinessException("배정방(assignedRoom)은 6자리여야 합니다.");
        }
        this.assignedRoom = assignedRoom;
        if (dormitory == null) {
            throw new BusinessException("기숙사(dormitory)는 필수 값입니다.");
        }
        this.dormitory = dormitory;

        if (floorNo < 2 || floorNo > 10) {
            throw new BusinessException("층수는 2층부터 10층까지만 존재합니다.");
        }
        this.floorNo = floorNo;
    }

    /**
     * 숫자(roomNo) 혹은 문자(dormitoryCode)를 사용해서 기숙사를 구분한다.
     */
    public enum Dormitory {
        // 미가엘, 행복기숙사
        MICHAEL(1, "A"), HAENGBOK(2, "B")
        ;
        public final int roomNo;
        public final String dormitoryCode;

        Dormitory(int roomNo, String dormitoryCode) {
            this.roomNo = roomNo;
            this.dormitoryCode = dormitoryCode;
        }
    }
}
