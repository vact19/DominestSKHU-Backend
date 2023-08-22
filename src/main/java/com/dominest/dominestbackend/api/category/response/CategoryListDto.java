package com.dominest.dominestbackend.api.category.response;

import com.dominest.dominestbackend.domain.post.component.category.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;


public class CategoryListDto {
    @Getter
    public static class Res {
        List<CategoryDto> categories;

        public static Res from(List<Category> categories){
            return new Res(CategoryDto.from(categories));
        }
        Res(List<CategoryDto> categories){
            this.categories = categories;
        }

        @Builder
        @Getter
        private static class CategoryDto{
            Long id;
            String name; // 카테고리 이름
            Type type;
            String explanation; // 카테고리 상세설명

            static CategoryDto from(Category category) {
                return CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .type(category.getType())
                        .explanation(category.getExplanation())
                        .build();
            }

            static List<CategoryDto> from(List<Category> categories) {
                return categories.stream()
                        .map(CategoryDto::from)
                        .collect(Collectors.toList());
            }
        }
    }



}
