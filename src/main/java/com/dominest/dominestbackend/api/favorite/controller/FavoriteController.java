package com.dominest.dominestbackend.api.favorite.controller;

import com.dominest.dominestbackend.api.common.ResTemplate;
import com.dominest.dominestbackend.api.favorite.dto.FavoriteListDto;
import com.dominest.dominestbackend.domain.favorite.FavoriteService;
import com.dominest.dominestbackend.domain.post.component.category.repository.CategoryRepository;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.security.Principal;

@RequiredArgsConstructor
@RestController
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    // 즐겨찾기 추가 / 취소
    @PostMapping("/categories/{categoryId}/favorites")
    public ResTemplate<String>  handleAddOrUndoFavorite(@PathVariable Long categoryId, Principal principal) {
        boolean isOn = favoriteService.addOrUndo(categoryId, principal.getName());
        String resMsg = isOn ? "즐겨찾기 추가" : "즐겨찾기 취소";
        return new ResTemplate<>(HttpStatus.OK, resMsg);
    }

    // 토큰을 소유한 유저의 즐찾목록 전체 조회
    @GetMapping("/favorites")
    public ResTemplate<?> handleGetAllFavorites(@NotNull(message = "인증 정보가 없습니다.") Principal principal) {
        FavoriteListDto.Res resDto = favoriteService.getAllByUserEmail(principal.getName());
        return new ResTemplate<>(HttpStatus.OK, "즐겨찾기 목록 조회"
                , resDto);
    }
}




















