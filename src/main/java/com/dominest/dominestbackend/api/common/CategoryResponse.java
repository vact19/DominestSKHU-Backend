package com.dominest.dominestbackend.api.common;

import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {
    long id;
    String categoryName;
    Type type;
    String postsLink;

    public static CategoryResponse from(Category category){
        return CategoryResponse.builder()
                .id(category.getId())
                .categoryName(category.getName())
                .type(category.getType())
                .postsLink(category.getPostsLink())
                .build();
    }
}
