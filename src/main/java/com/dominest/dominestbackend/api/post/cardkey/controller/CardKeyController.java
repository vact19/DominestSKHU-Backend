package com.dominest.dominestbackend.api.post.cardkey.controller;

import com.dominest.dominestbackend.api.common.ResponseTemplate;
import com.dominest.dominestbackend.api.post.cardkey.response.CardKeyListResponse;
import com.dominest.dominestbackend.api.post.cardkey.request.CreateCardKeyRequest;
import com.dominest.dominestbackend.api.post.cardkey.request.UpdateCardKeyRequest;
import com.dominest.dominestbackend.domain.post.cardkey.entity.CardKey;
import com.dominest.dominestbackend.domain.post.cardkey.service.CardKeyService;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.global.util.PageBaseConverter;
import com.dominest.dominestbackend.global.util.PrincipalParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RequiredArgsConstructor
@RestController
public class CardKeyController {
    private final CardKeyService cardKeyService;
    private final CategoryService categoryService;

    // 등록
    @PostMapping("/categories/{categoryId}/posts/card-key")
    public ResponseEntity<ResponseTemplate<Void>> handleCreateCardKey(
            @RequestBody @Valid CreateCardKeyRequest request
            , @PathVariable Long categoryId, Principal principal
    ) {
        String email = PrincipalParser.toEmail(principal);
        long cardKeyId = cardKeyService.create(request, categoryId, email);
        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.CREATED
                , cardKeyId + "번 카드키 기록 작성");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseTemplate);
    }
    // 수정
    @PatchMapping("/card-keys/{cardKeyId}")
    public ResponseTemplate<Void> handleUpdateCardKey(
            @PathVariable Long cardKeyId, @RequestBody @Valid UpdateCardKeyRequest request
    ) {
        long updatedKeyId = cardKeyService.update(cardKeyId, request);

        return new ResponseTemplate<>(HttpStatus.OK, updatedKeyId + "번 카드키 기록 수정");
    }

    // 삭제
    // 카드키 기록 삭제
    @DeleteMapping("/card-keys/{cardKeyId}")
    public ResponseTemplate<Void> handleDeleteCardKey(
            @PathVariable Long cardKeyId
    ) {
        long deletedKeyId = cardKeyService.delete(cardKeyId);

        return new ResponseTemplate<>(HttpStatus.OK, deletedKeyId + "번 카드키 기록 삭제");
    }
    // 이름검색 (인덱스 만들고, '검색어%' 로 만들 것)
    // 카드키 목록 조회. 최신등록순
    @GetMapping("/categories/{categoryId}/posts/card-key")
    public ResponseTemplate<CardKeyListResponse> handleGetCardKeys(
            @PathVariable Long categoryId, @RequestParam(defaultValue = "1") int page
            , @RequestParam(required = false) String name
    ) {
        final int COMPLAINT_TYPE_PAGE_SIZE = 20;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageBaseConverter.of(page, COMPLAINT_TYPE_PAGE_SIZE, sort);

        Category category = categoryService.validateCategoryType(categoryId, Type.CARD_KEY);

        Page<CardKey> cardKeyPage = cardKeyService.getPage(category.getId(), pageable, name);

        CardKeyListResponse response = CardKeyListResponse.from(cardKeyPage, category);
        return new ResponseTemplate<>(HttpStatus.OK
                , "(생성일자 내림차순) 페이지  목록 조회 - " + response.getPage().getCurrentPage() + "페이지"
                , response);
    }
}
