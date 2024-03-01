package com.dominest.dominestbackend.api.post.sanitationcheck.response;


import com.dominest.dominestbackend.api.common.AuditLog;
import com.dominest.dominestbackend.api.common.CategoryResponse;
import com.dominest.dominestbackend.api.common.PageInfo;
import com.dominest.dominestbackend.domain.post.component.category.Category;
import com.dominest.dominestbackend.domain.post.sanitationcheck.SanitationCheckPost;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class CheckPostListResponse {

    CategoryResponse category;
    PageInfo page;

    List<CheckPostDto> posts;
    public static CheckPostListResponse from(Page<SanitationCheckPost> postPage, Category category){
        CategoryResponse categoryResponse = CategoryResponse.from(category);
        PageInfo pageInfo = PageInfo.from(postPage);

        List<CheckPostDto> posts
                = CheckPostDto.from(postPage);

        return new CheckPostListResponse(categoryResponse, pageInfo, posts);
    }

    @Builder
    @Getter
    private static class CheckPostDto {
        long id;
        String title;
        AuditLog auditLog;

        static CheckPostDto from(SanitationCheckPost post){
            return CheckPostDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .auditLog(AuditLog.from(post))
                    .build();
        }

        static List<CheckPostDto> from(Page<SanitationCheckPost> posts){
            return posts.stream()
                    .map(CheckPostDto::from)
                    .collect(Collectors.toList());
        }
    }
}
