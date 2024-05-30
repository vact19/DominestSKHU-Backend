package com.dominest.dominestbackend.api.post.inspection.response;


import com.dominest.dominestbackend.api.common.AuditLog;
import com.dominest.dominestbackend.api.common.CategoryResponse;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.inspection.floor.entity.InspectionFloor;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// 지정한 게시글을 클릭하면 층 목록이 반환되는데, 이때의 층 목록을 반환하는 DTO
@AllArgsConstructor
@Getter
public class InspectionFloorListResponse {
    CategoryResponse category;
    List<CheckFloorDto> posts;

    public static InspectionFloorListResponse from(List<InspectionFloor> inspectionFloors, Category category){
        CategoryResponse categoryResponse = CategoryResponse.from(category);

        List<CheckFloorDto> posts
                = CheckFloorDto.from(inspectionFloors);

        return new InspectionFloorListResponse(categoryResponse, posts);
    }

    @Builder
    @Getter
    private static class CheckFloorDto {
        long id;
        String floor;
        AuditLog auditLog;

        static CheckFloorDto from(InspectionFloor inspectionFloor, int floorNum){
            return CheckFloorDto.builder()
                    .id(inspectionFloor.getId())
                    .floor(String.format("%d층", floorNum))
                    .auditLog(AuditLog.from(inspectionFloor))
                    .build();
        }

        static List<CheckFloorDto> from(List<InspectionFloor> inspectionFloors){
            List<CheckFloorDto> floorDtos = new ArrayList<>();
            int floorNum = 2;
            for (InspectionFloor inspectionFloor : inspectionFloors) {
                floorDtos.add(CheckFloorDto.from(inspectionFloor, floorNum++));
            }
            return floorDtos;
        }
    }
}
