package com.dominest.dominestbackend.api.post.undeliveredparcel.response;

import com.dominest.dominestbackend.api.common.AuditLog;
import com.dominest.dominestbackend.api.common.CategoryResponse;
import com.dominest.dominestbackend.api.common.PageInfo;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.entity.UndeliveredParcelPost;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class UndeliveredParcelPostListResponse {
    CategoryResponse category;
    PageInfo page;

    List<UndeliveredParcelPostDto> posts;

    public static UndeliveredParcelPostListResponse from(Page<UndeliveredParcelPost> postPage, Category category){
        CategoryResponse categoryResponse = CategoryResponse.from(category);
        PageInfo pageInfo = PageInfo.from(postPage);

        List<UndeliveredParcelPostDto> posts
                = UndeliveredParcelPostDto.from(postPage);

        return new UndeliveredParcelPostListResponse(categoryResponse, pageInfo, posts);
    }

    @Builder
    @Getter
    private static class UndeliveredParcelPostDto {
        long id;
        String title;
        AuditLog auditLog;

        static UndeliveredParcelPostDto from(UndeliveredParcelPost post){
            return UndeliveredParcelPostDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .auditLog(AuditLog.from(post))
                    .build();
        }

        static List<UndeliveredParcelPostDto> from(Page<UndeliveredParcelPost> posts){
            return posts
                    .map(UndeliveredParcelPostDto::from)
                    .toList();
        }
    }
}
