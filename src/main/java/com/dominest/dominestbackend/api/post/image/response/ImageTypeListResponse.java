package com.dominest.dominestbackend.api.post.image.response;

import com.dominest.dominestbackend.api.common.CategoryResponse;
import com.dominest.dominestbackend.api.common.PageInfo;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.image.entity.ImageType;
import com.dominest.dominestbackend.global.util.PrincipalParser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ImageTypeListResponse {
    PageInfo page; // 페이징 정보
    List<ImageTypeDto> posts; // 게시글 목록
    CategoryResponse category; // 카테고리 정보

    public static ImageTypeListResponse from(List<ImageType> imageTypes, Category category, PageInfo pageInfo){
        CategoryResponse categoryResponse = CategoryResponse.from(category);
        List<ImageTypeDto> imageTypeDtos = ImageTypeDto.from(imageTypes);

        return new ImageTypeListResponse(pageInfo, imageTypeDtos, categoryResponse);
    }

    @Getter
    @Builder
    private static class ImageTypeDto {
        long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createTime;
        String title;
        String writer;

        static ImageTypeDto from(ImageType imageType){
            return ImageTypeDto.builder()
                    .id(imageType.getId())
                    .createTime(imageType.getCreateTime())
                    .title(imageType.getTitle())
                    .writer(PrincipalParser.toName(imageType.getCreatedBy()))
                    .build();
        }

        static List<ImageTypeDto> from(List<ImageType> imageTypes){
            return imageTypes.stream()
                    .map(ImageTypeDto::from)
                    .collect(Collectors.toList());
        }
    }
}
