package com.dominest.dominestbackend.api.post.image.response;

import com.dominest.dominestbackend.domain.post.image.entity.ImageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class ImageTypeDetailResponse {
    ImageTypeDto postDetail;

    public static ImageTypeDetailResponse from(ImageType imageType) {
        ImageTypeDto imageTypeDto = ImageTypeDto.builder()
                .createTime(imageType.getCreateTime())
                .updateTime(imageType.getLastModifiedTime())
                .title(imageType.getTitle())
                .writer(imageType.getWriter().getName())
                .imageUrls(imageType.getImageUrls())
                .build();
        return new ImageTypeDetailResponse(imageTypeDto);
    }

    @Getter
    @Builder
    private static class ImageTypeDto {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createTime;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime updateTime;
        String title;
        String writer;
        List<String> imageUrls;
    }
}
