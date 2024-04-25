package com.dominest.dominestbackend.api.post.image.response;

import com.dominest.dominestbackend.api.common.CategoryResponse;
import com.dominest.dominestbackend.api.common.PageInfo;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.image.entity.ImageType;
import com.dominest.dominestbackend.global.util.PrincipalParser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ImageTypeListResponse {
    PageInfo page; // 페이징 정보
    List<ImageTypeDto> posts; // 게시글 목록
    CategoryResponse category; // 카테고리 정보

    public static ImageTypeListResponse from(Page<ImageType> imageTypes, Category category){
        CategoryResponse categoryResponse = CategoryResponse.from(category);
        PageInfo pageInfo = PageInfo.from(imageTypes);
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

        static List<ImageTypeDto> from(Page<ImageType> imageTypes){
            return imageTypes
                    .map(ImageTypeDto::from)
                    .toList();
        }
    }
}
