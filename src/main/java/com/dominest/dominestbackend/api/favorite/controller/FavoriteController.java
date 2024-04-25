package com.dominest.dominestbackend.api.favorite.controller;

import com.dominest.dominestbackend.api.common.ResponseTemplate;
import com.dominest.dominestbackend.api.favorite.response.FavoriteListResponse;
import com.dominest.dominestbackend.domain.favorite.entity.Favorite;
import com.dominest.dominestbackend.domain.favorite.service.FavoriteService;
import com.dominest.dominestbackend.global.util.PrincipalParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class FavoriteController {
    private final FavoriteService favoriteService;

    // 즐겨찾기 추가 / 취소
    @PostMapping("/categories/{categoryId}/favorites")
    public ResponseTemplate<String> handleAddOrUndoFavorite(@PathVariable Long categoryId, Principal principal) {

        boolean isOn = favoriteService.addOrUndo(categoryId, PrincipalParser.toEmail(principal));

        String resMsg = isOn ? "즐겨찾기 추가" : "즐겨찾기 취소";
        return new ResponseTemplate<>(HttpStatus.OK, resMsg);
    }

    // 토큰을 소유한 유저의 즐찾목록 전체 조회
    @GetMapping("/favorites")
    public ResponseTemplate<FavoriteListResponse> handleGetAllFavorites(@NotNull(message = "인증 정보가 없습니다.") Principal principal) {

        Sort sort = Sort.by(Sort.Direction.DESC, "lastModifiedTime");
        List<Favorite> favorites = favoriteService.getAllByUserEmail(PrincipalParser.toEmail(principal), sort);

        FavoriteListResponse response = FavoriteListResponse.from(favorites);
        return new ResponseTemplate<>(HttpStatus.OK, "즐겨찾기 목록 조회"
                , response);
    }
}
