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
public class UndelivParcelPostListResponse {
    CategoryResponse category;
    PageInfo page;

    List<UndelivParcelPostDto> posts;

    public static UndelivParcelPostListResponse from(Page<UndeliveredParcelPost> postPage, Category category){
        CategoryResponse categoryResponse = CategoryResponse.from(category);
        PageInfo pageInfo = PageInfo.from(postPage);

        List<UndelivParcelPostDto> posts
                = UndelivParcelPostDto.from(postPage);

        return new UndelivParcelPostListResponse(categoryResponse, pageInfo, posts);
    }

    @Builder
    @Getter
    private static class UndelivParcelPostDto {
        long id;
        String title;
        AuditLog auditLog;

        static UndelivParcelPostDto from(UndeliveredParcelPost post){
            return UndelivParcelPostListResponse.UndelivParcelPostDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .auditLog(AuditLog.from(post))
                    .build();
        }

        static List<UndelivParcelPostDto> from(Page<UndeliveredParcelPost> posts){
            return posts
                    .map(UndelivParcelPostDto::from)
                    .toList();
        }
    }
}
