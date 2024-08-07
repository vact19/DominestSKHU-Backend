package com.dominest.dominestbackend.api.post.manual.controller;

import com.dominest.dominestbackend.api.common.ResponseTemplate;
import com.dominest.dominestbackend.api.post.manual.dto.CreateManualPostDto;
import com.dominest.dominestbackend.api.post.manual.dto.ManualPostListDto;
import com.dominest.dominestbackend.api.post.manual.dto.ReadManualDto;
import com.dominest.dominestbackend.api.post.manual.dto.UpdateManualPostDto;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.post.manual.entity.ManualPost;
import com.dominest.dominestbackend.domain.post.manual.service.ManualPostService;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.external.file.FileIOException;
import com.dominest.dominestbackend.global.util.FileManager;
import com.dominest.dominestbackend.global.util.PagingUtil;
import com.dominest.dominestbackend.global.util.PrincipalParser;
import com.dominest.dominestbackend.global.util.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;



@RequiredArgsConstructor
@RestController
public class ManualPostController {
    private final ManualPostService manualPostService;
    private final CategoryService categoryService;
    private final FileManager fileManager;
    private final VideoService videoService;

    //게시글 작성
    @PostMapping("/categories/{categoryId}/posts/manual")
    public ResponseEntity<ResponseTemplate<Void>> handleCreateManual(
            @PathVariable Long categoryId, Principal principal, @Valid CreateManualPostDto.Req reqDto

    ) {
        String email = PrincipalParser.toEmail(principal);

        long manualPostId = manualPostService.save(categoryId, reqDto, email);

        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.CREATED,
                manualPostId + "번 게시글 작성");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseTemplate);
    }

    //게시글 목록 조회
    @GetMapping("/categories/{categoryId}/posts/manual")
    public ResponseTemplate<ManualPostListDto.Res> handleGetManualPostList(
            @PathVariable Long categoryId, @RequestParam(defaultValue = "1") int page) {
        final int MANUAL_TYPE_PAGE_SIZE = 20;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PagingUtil.getPageable(page, MANUAL_TYPE_PAGE_SIZE, sort);

        Category category = categoryService.validateCategoryType(categoryId, Type.MANUAL);
        Page<ManualPost> postsPage = manualPostService.getPage(category.getId(), pageable);

        ManualPostListDto.Res resDto = ManualPostListDto.Res.from(postsPage, category);
        return new ResponseTemplate<>(HttpStatus.OK
                , "페이지 게시글 목록 조회 - " + resDto.getPage().getCurrentPage() + "페이지"
                ,resDto);
    }

    //게시글 삭제
    @DeleteMapping("/posts/manual/{manualPostId}")
    public ResponseEntity<ResponseTemplate<Void>> handleDeleteManualPost(
            @PathVariable Long manualPostId
    ) {
        long deletedPostId = manualPostService.delete(manualPostId);

        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.OK, deletedPostId + "번 게시글 삭제");
        return ResponseEntity.ok(responseTemplate);
    }

    //게시글 수정
    @PatchMapping("/posts/manual/{manualPostId}")
    public ResponseEntity<ResponseTemplate<Void>> handleUpdateManualPost(
            @PathVariable Long manualPostId, @Valid UpdateManualPostDto.Req reqDto
    ) {
        long updatedPostId = manualPostService.update(manualPostId, reqDto);

        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.OK, updatedPostId + "번 게시글 수정");
        return ResponseEntity.ok(responseTemplate);
    }

    //게시글 읽기
    //categoryId와 manualId가 맞지 않는 경우에 보안 조치는 굳이 안해도 될 것 같아서 생략
    @GetMapping("/categories/{categoryId}/posts/manual/{manualId}")
    public ResponseTemplate<ReadManualDto.Res> handleManualPost(
            @PathVariable Long manualId, @RequestParam(defaultValue = "1") int page) {

        ManualPost post = manualPostService.getByIdIncludeAllColumn(manualId);

        ReadManualDto.Res resDto = ReadManualDto.Res.from(post, page);
        return new ResponseTemplate<>(HttpStatus.OK
                , "manual 게시글 조회 - " + post.getId() + "번 게시글"
                ,resDto);

    }

    //첨부물 조회 부분은 controller을 따로 빼서 처리하는 것은 어떨까...
    @GetMapping("/posts/manual/image")
    public void getImage(HttpServletResponse response, @RequestParam(required = true) String filePath) {
        response.setContentType("image/*");
        getAnyFile(response, filePath);
    }

    @GetMapping("posts/manual/file")
    public void getFile(HttpServletResponse response, @RequestParam(required = true) String filePath) {
        response.setContentType("application/download");
        getAnyFile(response, filePath);
    }

    @GetMapping("posts/manual/video")
    public ResponseEntity<ResourceRegion> getVideo(HttpServletResponse response, @RequestHeader HttpHeaders headers, @RequestParam(required = true) String filePath) {
        Optional<HttpRange> httpRangeOptional = headers.getRange().stream().findFirst();
        ResourceRegion resourceRegion = videoService.getVideoResource(filePath, httpRangeOptional);
        MediaType mediaType = videoService.getMediaType();
        response.setContentType(mediaType.getType());

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(resourceRegion);
    }

    public void getAnyFile(HttpServletResponse response, String filePath) {
        byte[] bytes = fileManager.getByteArr(filePath);
        try {
            response.getOutputStream().write(bytes);
        } catch (IOException e) {
            throw new FileIOException(ErrorCode.FILE_CANNOT_BE_SENT, e);
        }
    }
}
