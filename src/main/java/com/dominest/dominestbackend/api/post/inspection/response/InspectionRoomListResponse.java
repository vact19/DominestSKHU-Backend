package com.dominest.dominestbackend.api.post.inspection.response;

import com.dominest.dominestbackend.api.common.AuditLog;
import com.dominest.dominestbackend.api.common.CategoryResponse;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.entity.InspectionRoom;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.component.InspectionResidentInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class InspectionRoomListResponse {
    CategoryResponse category;
    List<InspectionRoomDto> inspectionRooms;

    public static InspectionRoomListResponse from(List<InspectionRoom> inspectionRooms, Category category){
        CategoryResponse categoryResponse = CategoryResponse.from(category);

        List<InspectionRoomDto> inspectionRoomDtos
                = InspectionRoomDto.from(inspectionRooms);

        return new InspectionRoomListResponse(categoryResponse, inspectionRoomDtos);
    }

    @Builder
    @Getter
    static class InspectionRoomDto {
        long id;
        boolean emptyRoom;
        String assignedRoom;
        ResidentDto resident;

        boolean indoor;
        boolean leavedTrash;
        boolean toilet;
        boolean shower;
        boolean prohibitedItem;

        InspectionRoom.PassState passState;
        String etc;

        AuditLog auditLog;

        static InspectionRoomDto from(InspectionRoom inspectionRoom){
            InspectionResidentInfo inspectionResidentInfo = inspectionRoom.getInspectionResidentInfo();
            ResidentDto residentDto = null;
            boolean emptyRoom = true;

            if (inspectionResidentInfo != null) {
                 residentDto = ResidentDto.builder()
                        .name(inspectionResidentInfo.getName())
                        .studentId(inspectionResidentInfo.getStudentId())
                        .phoneNo(inspectionResidentInfo.getPhoneNo())
                        .penalty(inspectionRoom.getPassState().getPenalty())
                        .build();
                 emptyRoom = false;
            }

            return InspectionRoomDto.builder()
                    .id(inspectionRoom.getId())
                    .emptyRoom(emptyRoom)
                    .assignedRoom(inspectionRoom.getRoom().getAssignedRoom())
                    .resident(residentDto)
                    .indoor(inspectionRoom.isIndoor())
                    .leavedTrash(inspectionRoom.isLeavedTrash())
                    .toilet(inspectionRoom.isToilet())
                    .shower(inspectionRoom.isShower())
                    .prohibitedItem(inspectionRoom.isProhibitedItem())
                    .passState(inspectionRoom.getPassState())
                    .etc(inspectionRoom.getEtc())
                    .auditLog(AuditLog.from(inspectionRoom))
                    .build();
        }

        static List<InspectionRoomDto> from(List<InspectionRoom> rooms){
            return rooms.stream()
                    .map(InspectionRoomDto::from)
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

