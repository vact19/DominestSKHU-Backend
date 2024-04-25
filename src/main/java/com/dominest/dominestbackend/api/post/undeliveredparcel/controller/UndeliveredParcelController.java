package com.dominest.dominestbackend.api.post.undeliveredparcel.controller;

import com.dominest.dominestbackend.api.common.ResponseTemplate;
import com.dominest.dominestbackend.api.post.undeliveredparcel.request.CreateUndelivParcelRequest;
import com.dominest.dominestbackend.api.post.undeliveredparcel.response.UndelivParcelPostDetailResponse;
import com.dominest.dominestbackend.api.post.undeliveredparcel.response.UndelivParcelPostListResponse;
import com.dominest.dominestbackend.api.post.undeliveredparcel.request.UpdateUndelivParcelDtoRequest;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.entity.UndeliveredParcelPost;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.service.UndeliveredParcelPostService;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.component.service.UndeliveredParcelService;
import com.dominest.dominestbackend.global.util.PageBaseConverter;
import com.dominest.dominestbackend.global.util.PrincipalParser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.security.Principal;

@RequiredArgsConstructor
@RestController
public class UndeliveredParcelController {
    private final UndeliveredParcelPostService undelivParcelPostService;
    private final UndeliveredParcelService undeliveredParcelService;
    private final CategoryService categoryService;

    // 게시글 등록
    @PostMapping("/categories/{categoryId}/posts/undelivered-parcel")
    public ResponseEntity<ResponseTemplate<Void>> handleCreateParcelPost(
            @PathVariable Long categoryId, Principal principal
    ) {
        String email = PrincipalParser.toEmail(principal);
        long unDeliParcelId = undelivParcelPostService.create(categoryId, email);
        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.CREATED, unDeliParcelId + "번 게시글 작성");

        return ResponseEntity
                .created(URI.create("/categories/"+categoryId+"/posts/undelivered-parcel/" + unDeliParcelId))
                .body(responseTemplate);
    }

    //  게시글 목록 조회
    @GetMapping("/categories/{categoryId}/posts/undelivered-parcel")
    public ResponseTemplate<UndelivParcelPostListResponse> handleGetParcelPosts(
            @PathVariable Long categoryId, @RequestParam(defaultValue = "1") int page
    ) {
        final int IMAGE_TYPE_PAGE_SIZE = 20;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageBaseConverter.of(page, IMAGE_TYPE_PAGE_SIZE, sort);

        Category category = categoryService.validateCategoryType(categoryId, Type.UNDELIVERED_PARCEL_REGISTER);
        Page<UndeliveredParcelPost> postsPage = undelivParcelPostService.getPage(category.getId(), pageable);

        UndelivParcelPostListResponse response = UndelivParcelPostListResponse.from(postsPage, category);
        return new ResponseTemplate<>(HttpStatus.OK
                , "페이지 게시글 목록 조회 - " + response.getPage().getCurrentPage() + "페이지"
                , response);
    }

    // 게시글 상세 조회
    @GetMapping("/posts/undelivered-parcel/{undelivParcelPostId}")
    public ResponseTemplate<UndelivParcelPostDetailResponse> handleGetParcels(
            @PathVariable Long undelivParcelPostId
    ) {
        UndeliveredParcelPost undelivParcelPost = undelivParcelPostService.getByIdFetchParcels(undelivParcelPostId);

        UndelivParcelPostDetailResponse response = UndelivParcelPostDetailResponse.from(undelivParcelPost);
        return new ResponseTemplate<>(HttpStatus.OK, "택배 관리대장 게시물 상세조회", response);
    }

    // 제목 변경
    @PatchMapping("/posts/undelivered-parcel/{undelivParcelPostId}")
    public ResponseTemplate<UndelivParcelPostDetailResponse> handleRenamePost(
            @PathVariable Long undelivParcelPostId,
            @RequestBody @Valid UndeliveredParcelController.PostTitleRequest request
    ) {
        undelivParcelPostService.renameTitle(undelivParcelPostId, request.getTitle());
        return new ResponseTemplate<>(HttpStatus.OK,
                String.format("제목 변경 -> %s", request.getTitle())
        );
    }

    @NoArgsConstructor
    @Getter
    public static class PostTitleRequest {
        @NotBlank(message = "변경할 제목을 입력해주세요.")
        private String title;
    }

    // 게시글 삭제
    @DeleteMapping("/posts/undelivered-parcel/{undelivParcelPostId}")
    public ResponseEntity<ResponseTemplate<Void>> handleDeleteParcelPost(
            @PathVariable Long undelivParcelPostId
    ) {
        long deletedPostId = undelivParcelPostService.delete(undelivParcelPostId);

        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.OK, deletedPostId + "번 게시글 삭제");
        return ResponseEntity.ok(responseTemplate);
    }

    // 게시글 내부 관리목록에 관리물품 등록
    @PostMapping("/posts/undelivered-parcel/{undelivParcelPostId}")
    public ResponseEntity<ResponseTemplate<Void>> handleCreateParcel(
                @PathVariable Long undelivParcelPostId,
                @RequestBody @Valid CreateUndelivParcelRequest request
    ) {
        Long undelivParcelId = undeliveredParcelService.create(undelivParcelPostId, request);

        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.CREATED,
                undelivParcelPostId + "번 관리대장 게시글에" +  undelivParcelId + "번 관리물품 작성");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseTemplate);
    }

    // 관리물품 단건 수정
    @PatchMapping("/undeliv-parcels/{undelivParcelId}")
    public ResponseTemplate<Void> handleUpdateParcel(
            @PathVariable Long undelivParcelId, @RequestBody @Valid UpdateUndelivParcelDtoRequest request
    ) {
        // parcelId 조회, 값 바꿔치기, 저장하기
        long updatedId = undeliveredParcelService.update(undelivParcelId, request);

        return new ResponseTemplate<>(HttpStatus.OK, updatedId + "번 관리물품 수정");
    }

    // 관리물품 단건 삭제
    @DeleteMapping("/undeliv-parcels/{undelivParcelId}")
    public ResponseTemplate<Void> handleDeleteParcel(
            @PathVariable Long undelivParcelId
    ) {
        long deleteId = undeliveredParcelService.delete(undelivParcelId);

        return new ResponseTemplate<>(HttpStatus.OK, deleteId + "번 관리물품 삭제");
    }
}
