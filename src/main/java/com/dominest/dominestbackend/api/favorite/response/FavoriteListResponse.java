package com.dominest.dominestbackend.api.favorite.response;

import com.dominest.dominestbackend.domain.favorite.entity.Favorite;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class FavoriteListResponse {
    List<FavoriteDto> favorites;

    public static FavoriteListResponse from(List<Favorite> favorites) {
        List<FavoriteDto> favoriteDtos = FavoriteDto.from(favorites);
        return new FavoriteListResponse(favoriteDtos);
    }

    @Builder
    @Getter
    // 즐겨찾기 ID, 카테고리 이름, 카테고리 링크
    private static class FavoriteDto {
        long id;
        String categoryName;
        String categoryLink;
        long categoryId;

        static FavoriteDto from(Favorite favorite) {
            Category category = favorite.getCategory();
            return FavoriteDto.builder()
                    .id(favorite.getId())
                    .categoryName(category.getName())
                    .categoryLink(category.getPostsLink())
                    .categoryId(category.getId())
                    .build();
        }

        static List<FavoriteDto> from(List<Favorite> favorites) {
            return favorites.stream()
                    .map(FavoriteDto::from)
                    .collect(Collectors.toList());
        }
    }
}
