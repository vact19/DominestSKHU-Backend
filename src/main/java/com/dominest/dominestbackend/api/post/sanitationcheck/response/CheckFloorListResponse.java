package com.dominest.dominestbackend.api.post.sanitationcheck.response;


import com.dominest.dominestbackend.api.common.AuditLog;
import com.dominest.dominestbackend.api.common.CategoryResponse;
import com.dominest.dominestbackend.domain.post.component.category.Category;
import com.dominest.dominestbackend.domain.post.sanitationcheck.floor.Floor;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// 지정한 게시글을 클릭하면 층 목록이 반환되는데, 이때의 층 목록을 반환하는 DTO
@AllArgsConstructor
@Getter
public class CheckFloorListResponse {
    CategoryResponse category;
    List<CheckFloorDto> posts;

    public static CheckFloorListResponse from(List<Floor> floors, Category category){
        CategoryResponse categoryResponse = CategoryResponse.from(category);

        List<CheckFloorDto> posts
                = CheckFloorDto.from(floors);

        return new CheckFloorListResponse(categoryResponse, posts);
    }

    @Builder
    @Getter
    private static class CheckFloorDto {
        long id;
        String floor;
        AuditLog auditLog;

        static CheckFloorDto from(Floor floor, int floorNum){
            return CheckFloorDto.builder()
                    .id(floor.getId())
                    .floor(String.format("%d층", floorNum))
                    .auditLog(AuditLog.from(floor))
                    .build();
        }

        static List<CheckFloorDto> from(List<Floor> floors){
            List<CheckFloorDto> floorDtos = new ArrayList<>();
            int floorNum = 2;
            for (Floor floor : floors) {
                floorDtos.add(CheckFloorDto.from(floor, floorNum++));
            }
            return floorDtos;
        }
    }
}
