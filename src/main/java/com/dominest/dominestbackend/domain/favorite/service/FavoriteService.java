package com.dominest.dominestbackend.domain.favorite.service;

import com.dominest.dominestbackend.domain.favorite.repository.FavoriteRepository;
import com.dominest.dominestbackend.domain.favorite.entity.Favorite;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    @Transactional
    // 즐겨찾기 추가 / 취소
    public boolean addOrUndo(Long categoryId, String userEmail) {
        // 일단 조인해. 없으면 예외처리해.
        Favorite favorite = favoriteRepository.findByCategoryIdAndUserEmail(categoryId, userEmail);

        if (favorite == null) {
            User user = userRepository.getByEmail(userEmail);
            Category category = categoryService.getById(categoryId);

            Favorite newFavorite = Favorite.builder()
                    .user(user)
                    .category(category)
                    .build();
            favoriteRepository.save(newFavorite);
            return true;  // 추가되었으므로 add를 의미하는 true 반환
        }

        return favorite.switchOnOff();
    }

    public List<Favorite> getAllByUserEmail(String email, Sort sort) {
        // Favorite을  User, Category와 JOIN 한다.
        return favoriteRepository.findAllByUserEmailFetchCategoryOrderByUpdateTimeDesc(email, sort);
    }
}
