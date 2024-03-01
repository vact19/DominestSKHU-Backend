package com.dominest.dominestbackend.api.post.sanitationcheck.response;

import com.dominest.dominestbackend.api.common.AuditLog;
import com.dominest.dominestbackend.api.common.CategoryResponse;
import com.dominest.dominestbackend.domain.post.component.category.Category;
import com.dominest.dominestbackend.domain.post.sanitationcheck.floor.checkedroom.CheckedRoom;
import com.dominest.dominestbackend.domain.post.sanitationcheck.floor.checkedroom.component.ResidentInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class CheckedRoomListResponse {
    CategoryResponse category;
    List<CheckedRoomDto> checkedRooms;

    public static CheckedRoomListResponse from(List<CheckedRoom> checkedRooms, Category category){
        CategoryResponse categoryResponse = CategoryResponse.from(category);

        List<CheckedRoomDto> checkedRoomDtos
                = CheckedRoomDto.from(checkedRooms);

        return new CheckedRoomListResponse(categoryResponse, checkedRoomDtos);
    }

    @Builder
    @Getter
    static class CheckedRoomDto {
        long id;
        boolean emptyRoom;
        String assignedRoom;
        ResidentDto resident;

        boolean indoor;
        boolean leavedTrash;
        boolean toilet;
        boolean shower;
        boolean prohibitedItem;

        CheckedRoom.PassState passState;
        String etc;

        AuditLog auditLog;

        static CheckedRoomDto from(CheckedRoom checkedRoom){
            ResidentInfo residentInfo = checkedRoom.getResidentInfo();
            ResidentDto residentDto = null;
            boolean emptyRoom = true;

            if (residentInfo != null) {
                 residentDto = ResidentDto.builder()
                        .name(residentInfo.getName())
                        .studentId(residentInfo.getStudentId())
                        .phoneNo(residentInfo.getPhoneNo())
                        .penalty(checkedRoom.getPassState().getPenalty())
                        .build();
                 emptyRoom = false;
            }

            return CheckedRoomDto.builder()
                    .id(checkedRoom.getId())
                    .emptyRoom(emptyRoom)
                    .assignedRoom(checkedRoom.getRoom().getAssignedRoom())
                    .resident(residentDto)
                    .indoor(checkedRoom.isIndoor())
                    .leavedTrash(checkedRoom.isLeavedTrash())
                    .toilet(checkedRoom.isToilet())
                    .shower(checkedRoom.isShower())
                    .prohibitedItem(checkedRoom.isProhibitedItem())
                    .passState(checkedRoom.getPassState())
                    .etc(checkedRoom.getEtc())
                    .auditLog(AuditLog.from(checkedRoom))
                    .build();
        }

        static List<CheckedRoomDto> from(List<CheckedRoom> rooms){
            return rooms.stream()
                    .map(CheckedRoomDto::from)
                    .collect(Collectors.toList());
        }

        @Getter
        @Builder
        static class ResidentDto {
            String name;
            String studentId;
            // 클라이언트단에서 필드명 'phon' 요구
            @JsonProperty("phon")
            String phoneNo;
            Integer penalty;
        }
    }
}

