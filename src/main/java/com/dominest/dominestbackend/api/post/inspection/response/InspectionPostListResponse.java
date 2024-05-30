package com.dominest.dominestbackend.api.post.inspection.response;


import com.dominest.dominestbackend.api.common.AuditLog;
import com.dominest.dominestbackend.api.common.CategoryResponse;
import com.dominest.dominestbackend.api.common.PageInfo;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.inspection.entity.InspectionPost;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class InspectionPostListResponse {

    CategoryResponse category;
    PageInfo page;

    List<CheckPostDto> posts;
    public static InspectionPostListResponse from(Page<InspectionPost> postPage, Category category){
        CategoryResponse categoryResponse = CategoryResponse.from(category);
        PageInfo pageInfo = PageInfo.from(postPage);

        List<CheckPostDto> posts
                = CheckPostDto.from(postPage);

        return new InspectionPostListResponse(categoryResponse, pageInfo, posts);
    }

    @Builder
    @Getter
    private static class CheckPostDto {
        long id;
        String title;
        AuditLog auditLog;

        static CheckPostDto from(InspectionPost post){
            return CheckPostDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .auditLog(AuditLog.from(post))
                    .build();
        }

        static List<CheckPostDto> from(Page<InspectionPost> posts){
            return posts.stream()
                    .map(CheckPostDto::from)
                    .collect(Collectors.toList());
        }
    }
}
