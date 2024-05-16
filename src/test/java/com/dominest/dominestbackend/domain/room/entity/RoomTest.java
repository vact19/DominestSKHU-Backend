package com.dominest.dominestbackend.domain.room.entity;

import com.dominest.dominestbackend.global.exception.exceptions.business.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class RoomTest {
    @DisplayName("배정방이 null일 경우 예외가 발생한다")
    @Test
    void newRoom_assignedRoomIsNull() {
        //given
        String assignedRoom = null;

        //when-then
        assertThatThrownBy(() -> new Room(assignedRoom, 2, Room.Dormitory.HAENGBOK))
                .isInstanceOf(BusinessException.class)
                .hasMessage("배정방(assignedRoom)은 6자리여야 합니다.");
    }

    @DisplayName("배정방의 길이가 6자리가 아닐 경우 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"55555", "77777", "019-123-4567"})
    void newRoom_assignedRoomLengthInValid(String assignedRoom) {
        //given - parameter assignedRoom:String

        //when-then
        assertThatThrownBy(() -> new Room(assignedRoom, 2, Room.Dormitory.HAENGBOK))
                .isInstanceOf(BusinessException.class)
                .hasMessage("배정방(assignedRoom)은 6자리여야 합니다.");
    }

    @DisplayName("층수가 2~10층이 아니면 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1, 11})
    void newRoom_assignedRoomLengthNotMatched(int floorNo) {
        //given - parameter floorNo:int

        //when-then
        assertThatThrownBy(() -> new Room("B1042A", floorNo, Room.Dormitory.HAENGBOK))
                .isInstanceOf(BusinessException.class)
                .hasMessage("층수는 2층부터 10층까지만 존재합니다.");
    }

    @DisplayName("배정방, 층, 기숙사 분류로 Room을 생성할 수 있다")
    @Test
    void newRoom() {
        //given
        String assignedRoom = "B1049A";
        int floorNo = 2;
        Room.Dormitory dormitory = Room.Dormitory.HAENGBOK;

        //when
        Room room = new Room(assignedRoom, floorNo, dormitory);

        //then
        assertThat(room.getAssignedRoom()).isEqualTo(assignedRoom);
        assertThat(room.getFloorNo()).isEqualTo(floorNo);
        assertThat(room.getDormitory()).isEqualTo(dormitory);
    }
}
