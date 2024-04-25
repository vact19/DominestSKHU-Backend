package com.dominest.dominestbackend.api.post.manual.dto;

import com.dominest.dominestbackend.api.common.AuditLog;
import com.dominest.dominestbackend.api.common.CategoryResponse;
import com.dominest.dominestbackend.api.common.PageInfo;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.manual.entity.ManualPost;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ManualPostListDto {
    @Getter
    public static class Res {
        CategoryResponse category;
        PageInfo page;

        Set<ManualPostDto> posts;

        public static Res from(Page<ManualPost> postPage, Category category){
            CategoryResponse categoryResponse = CategoryResponse.from(category);
            PageInfo pageInfo = PageInfo.from(postPage);

            Set<ManualPostDto> posts
                    = ManualPostDto.from(postPage);

            return new Res(pageInfo, posts, categoryResponse);
        }

        Res(PageInfo page, Set<ManualPostDto> posts, CategoryResponse category) {
            this.page = page;
            this.posts = posts;
            this.category = category;
        }

        @Builder
        @Getter
        static class ManualPostDto {
            long id;
            String title;
            String writerName;
            boolean isModified;
            LocalDateTime createTime;
            AuditLog auditLog;

            static ManualPostDto from(ManualPost post){
                return ManualPostListDto.Res.ManualPostDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .writerName(post.getWriter().getName())
                        .isModified(post.isModified())
                        .createTime(post.getCreateTime())
                        .auditLog(AuditLog.from(post))
                        .build();
            }

            static Set<ManualPostDto> from(Page<ManualPost> posts){
                return posts.stream()
                        .map(ManualPostDto::from)
                        .collect(Collectors.toSet());
            }
        }
    }
}
