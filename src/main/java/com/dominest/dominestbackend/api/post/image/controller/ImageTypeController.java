package com.dominest.dominestbackend.api.post.image.controller;

import com.dominest.dominestbackend.api.common.ResponseTemplate;
import com.dominest.dominestbackend.api.post.image.response.ImageTypeDetailResponse;
import com.dominest.dominestbackend.api.post.image.response.ImageTypeListResponse;
import com.dominest.dominestbackend.api.post.image.request.SaveImageTypeRequest;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.post.image.entity.ImageType;
import com.dominest.dominestbackend.domain.post.image.service.ImageTypeService;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.external.file.FileIOException;
import com.dominest.dominestbackend.global.util.FileManager;
import com.dominest.dominestbackend.global.util.PageBaseConverter;
import com.dominest.dominestbackend.global.util.PrincipalParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ImageTypeController {

    private final ImageTypeService imageTypeService;
    private final FileManager fileManager;
    private final CategoryService categoryService;

    //    1. 제목
    //  2. 작성자(user) - 외래키
    //  3. url 리스트
    // 이미지 게시물 작성
    @PostMapping("/categories/{categoryId}/posts/image-types")
    public ResponseEntity<ResponseTemplate<Void>> handleCreateImageType(
            @Valid SaveImageTypeRequest request,
            @PathVariable Long categoryId, Principal principal) {
        // 이미지 저장
        String email = PrincipalParser.toEmail(principal);
        long imageTypeId = imageTypeService.create(request, categoryId, email);
        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.CREATED, imageTypeId + "번 게시글 작성");

        return ResponseEntity
                .created(URI.create("/posts/image-types/" + imageTypeId))
                .body(responseTemplate);
    }

    /**
     *  게시글 수정
     *  원본 게시글 데이터를 받아서 업데이트.
     *  최초 생성자 이름은 유지하지만, 수정 시 권한을 체크하지 않고 수정자 이름만 변경한다.
     */
    @PatchMapping("/posts/image-types/{imageTypeId}")
    public ResponseTemplate<Void> handleUpdateImageType(
            @PathVariable Long imageTypeId,
            @Valid SaveImageTypeRequest request
    ) {
        long updatedImageTypeId = imageTypeService.update(request, imageTypeId);
        return new ResponseTemplate<>(HttpStatus.OK, updatedImageTypeId + "번 게시글 수정");
    }

    /**
     *  게시글 삭제. 권한을 체크하지 않는다.
     */
    @DeleteMapping("/posts/image-types/{imageTypeId}")
    public ResponseTemplate<Void> handleDeleteImageType(@PathVariable Long imageTypeId) {
        ImageType imageType = imageTypeService.deleteById(imageTypeId);

        List<String> imageUrlsToDelete = imageType.getImageUrls();
        fileManager.deleteFile(FileManager.FilePrefix.POST_IMAGE_TYPE, imageUrlsToDelete);

        return new ResponseTemplate<>(HttpStatus.OK, imageType.getId() + "번 게시글 삭제");
    }

    // 게시물 이미지 조회
    @GetMapping("/posts/image-types/images")
    public void getImage(HttpServletResponse response, @RequestParam(required = true) String filename) {
        byte[] bytes = fileManager.getByteArr(FileManager.FilePrefix.POST_IMAGE_TYPE, filename);

        response.setContentType("image/*");
        try {
            response.getOutputStream().write(bytes);
        } catch (IOException e) {
            throw new FileIOException(ErrorCode.FILE_CANNOT_BE_SENT, e);
        }
    }

    // 게시물 단건 조회
    @GetMapping("/categories/{categoryId}/posts/image-types/{imageTypeId}")
    public ResponseTemplate<ImageTypeDetailResponse> handleGetImageType(
            @PathVariable Long categoryId, @PathVariable Long imageTypeId
    ) {
        ImageType imageType = imageTypeService.getById(imageTypeId);

        ImageTypeDetailResponse response = ImageTypeDetailResponse.from(imageType);
        return new ResponseTemplate<>(HttpStatus.OK
                , imageTypeId+"번 게시물  조회 성공"
                , response);
    }

    // 게시물 목록을 조회한다.
    @GetMapping("/categories/{categoryId}/posts/image-types")
    public ResponseTemplate<ImageTypeListResponse> handleGetImageTypes(@PathVariable Long categoryId, @RequestParam(defaultValue = "1") int page) {
        final int IMAGE_TYPE_PAGE_SIZE = 20;
        Pageable pageable = PageBaseConverter.of(page, IMAGE_TYPE_PAGE_SIZE);

        Category category = categoryService.validateCategoryType(categoryId, Type.IMAGE);
        // 카테고리 내 게시글이 1건도 없는 경우도 있으므로, 게시글과 함께 카테고리를 Join해서 데이터를 찾아오지 않는다.
        Page<ImageType> imageTypes = imageTypeService.getPage(categoryId, pageable);

        ImageTypeListResponse response = ImageTypeListResponse.from(imageTypes, category);
        return new ResponseTemplate<>(HttpStatus.OK
                , "페이지 게시글 목록 조회 - " + response.getPage().getCurrentPage() + "페이지"
                , response);
    }
}














