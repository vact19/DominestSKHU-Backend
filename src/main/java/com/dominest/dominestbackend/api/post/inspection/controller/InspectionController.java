package com.dominest.dominestbackend.api.post.inspection.controller;

import com.dominest.dominestbackend.api.common.ResponseTemplate;
import com.dominest.dominestbackend.api.post.inspection.response.InspectionFloorListResponse;
import com.dominest.dominestbackend.api.post.inspection.response.InspectionPostListResponse;
import com.dominest.dominestbackend.api.post.inspection.response.InspectionRoomListResponse;
import com.dominest.dominestbackend.api.post.inspection.request.UpdateInspectionRoomRequest;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.post.inspection.entity.InspectionPost;
import com.dominest.dominestbackend.domain.post.inspection.service.InspectionPostService;
import com.dominest.dominestbackend.domain.post.inspection.floor.entity.InspectionFloor;
import com.dominest.dominestbackend.domain.post.inspection.floor.service.InspectionFloorService;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.entity.InspectionRoom;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.repository.InspectionRoomRepository;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.service.InspectionRoomService;
import com.dominest.dominestbackend.domain.resident.support.ResidentExcelParser;
import com.dominest.dominestbackend.domain.resident.entity.component.ResidenceSemester;
import com.dominest.dominestbackend.global.util.FileManager.FileExt;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.security.Principal;
import java.util.List;

import static com.dominest.dominestbackend.domain.post.inspection.floor.room.entity.InspectionRoom.PassState.NOT_PASSED;

@RequiredArgsConstructor
@RestController
public class InspectionController {
    private final InspectionPostService inspectionPostService;
    private final CategoryService categoryService;
    private final InspectionFloorService inspectionFloorService;
    private final InspectionRoomService inspectionRoomService;
    private final InspectionRoomRepository inspectionRoomRepository;
    private final ResidentExcelParser residentExcelParser;

    // 게시글 생성(학기 지정)
    @PostMapping("/categories/{categoryId}/posts/inspection")
    public ResponseEntity<ResponseTemplate<Void>> handleCreateInspectionPost(
            @PathVariable Long categoryId, Principal principal
            , @RequestBody @Valid ResidenceSemesterDto residenceSemesterDto
    ) {
        String email = PrincipalParser.toEmail(principal);

        long inspectionPostId = inspectionPostService.create(
                residenceSemesterDto.getResidenceSemester()
                , categoryId, email);

        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.CREATED, inspectionPostId + "번 게시글 작성");
        return ResponseEntity
                .created(URI.create("/categories/"+categoryId+"/posts/inspection/" + inspectionPostId))
                .body(responseTemplate);
    }
    @Getter
    @NoArgsConstructor
    public static class ResidenceSemesterDto {
        @NotNull(message = "학기를 선택해주세요.")
        ResidenceSemester residenceSemester;
    }


    // 게시글 제목 수정
    @PatchMapping("/posts/inspection/{postId}")
    public ResponseEntity<ResponseTemplate<Void>> handleUpdateInspectionPostTitle(
            @PathVariable Long postId, @RequestBody @Valid TitleDto titleDto
    ) {
        long updatedPostId = inspectionPostService.updateTitle(postId, titleDto.getTitle());

        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.OK, updatedPostId + "번 게시글 제목 수정");
        return ResponseEntity.ok(responseTemplate);
    }
    @Getter
    @NoArgsConstructor
    public static class TitleDto {
        @NotBlank(message = "제목을 입력해주세요.")
        String title;
    }

    // 게시글 삭제
    @DeleteMapping("/posts/inspection/{postId}")
    public ResponseEntity<ResponseTemplate<Void>> handleDeletePost(
            @PathVariable Long postId
    ) {
        inspectionPostService.delete(postId);

        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.OK, "방역호실점검 게시글 삭제 완료");
        return ResponseEntity.ok(responseTemplate);
    }

    // 게시글 목록
    @GetMapping("/categories/{categoryId}/posts/inspection")
    public ResponseTemplate<InspectionPostListResponse> handleGetInspectionPosts(
            @PathVariable Long categoryId, @RequestParam(defaultValue = "1") int page
    ) {
        final int IMAGE_TYPE_PAGE_SIZE = 20;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageBaseConverter.of(page, IMAGE_TYPE_PAGE_SIZE, sort);

        Category category = categoryService.validateCategoryType(categoryId, Type.INSPECTION);
        Page<InspectionPost> postPage = inspectionPostService.getPage(category.getId(), pageable);

        InspectionPostListResponse response = InspectionPostListResponse.from(postPage, category);
        return new ResponseTemplate<>(HttpStatus.OK
                , "페이지 게시글 목록 조회 - " + response.getPage().getCurrentPage() + "페이지"
                , response);
    }

    // 게시글 상세조회 - 층 목록
    // posts inspection num floors
    @GetMapping("/posts/inspection/{postId}/floors")
    public ResponseTemplate<InspectionFloorListResponse> handleGetFloors(
            @PathVariable Long postId
    ) {
        List<InspectionFloor> inspectionFloors = inspectionFloorService.getAllByPostId(postId);
        Category category = inspectionPostService.getByIdFetchCategory(postId).getCategory();

        InspectionFloorListResponse response = InspectionFloorListResponse.from(inspectionFloors, category);
        return new ResponseTemplate<>(HttpStatus.OK
                , postId + "번 게시글의 층 목록 조회"
                , response);
    }

    // 층을 클릭해서 들어간 점검표 페이지
    // posts inspection num floors num
    @GetMapping("/posts/inspection/{postId}/floors/{floorId}")
    public ResponseTemplate<InspectionRoomListResponse> handleGetFloors(
            @PathVariable Long postId, @PathVariable Long floorId
    ) {
        Category category = inspectionPostService.getByIdFetchCategory(postId).getCategory();
        List<InspectionRoom> inspectionRooms = inspectionRoomService.getAllByFloorId(floorId);

        InspectionRoomListResponse response = InspectionRoomListResponse.from(inspectionRooms, category);
        return new ResponseTemplate<>(HttpStatus.OK
                , postId + "번 게시글, 층 ID: " + floorId + "의 점검표 조회"
                , response);
    }

    // 미통과자
    @GetMapping("/posts/inspection/{postId}/not-passed")
    public ResponseTemplate<InspectionRoomListResponse> handleGetNotPassed(
            @PathVariable Long postId
    ) {
        Category category = inspectionPostService.getByIdFetchCategory(postId).getCategory();
        // 여기서 미통과자를 입사생과 함께 조회
        List<InspectionRoom> inspectionRooms = inspectionRoomRepository.findNotPassedAllByPostId(postId, NOT_PASSED);

        InspectionRoomListResponse response = InspectionRoomListResponse.from(inspectionRooms, category);
        return new ResponseTemplate<>(HttpStatus.OK
                , postId + "번 게시글의 미통과자 목록 조회"
                , response);
    }

    // 컬럼 수정
    @PatchMapping("/inspection-rooms/{roomId}")
    public ResponseEntity<ResponseTemplate<Void>> handleUpdateInspectionRoom(
            @PathVariable Long roomId
            , @RequestBody UpdateInspectionRoomRequest request
    ) {
        inspectionRoomService.update(roomId, request);
        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.OK, "점검표 수정 완료");
        return ResponseEntity.ok(responseTemplate);
    }

    // 전체 통과
    @PatchMapping("/inspection-rooms/{roomId}/pass-all")
    public ResponseEntity<ResponseTemplate<Void>> handleUpdateInspectionRoomPass(
            @PathVariable Long roomId
    ) {
        inspectionRoomService.passAll(roomId);
        ResponseTemplate<Void> responseTemplate = new ResponseTemplate<>(HttpStatus.OK, "점검표 전체 통과 완료");
        return ResponseEntity.ok(responseTemplate);
    }

    // (엑셀 다운로드) 방호점 게시글에서 벌점이 부과된 입사생 목록
    @GetMapping("/posts/inspection/{postId}/xlsx-penalty-residents")
    public void handlePenaltyInspectionRoomExcelDownload(
            @PathVariable Long postId, HttpServletResponse response
    ) {
        String postTitle = inspectionPostService.getById(postId).getTitle();

        String filename = postTitle + " - 벌점 부여자 명단" + "." + FileExt.XLSX.label;
        String sheetName = "벌점 부여자 명단";

        // N차 통과를 조회하려면 InspectionRoom까지 조회해야 함. Resident를 Inner Join해서 빈 방 조회를 피하자.
        // 2~10차 통과자 목록 조회, Room 정보까지 Fetch Join함.
        List<InspectionRoom.PassState> penalty0passStates =
                List.of(NOT_PASSED, InspectionRoom.PassState.FIRST_PASSED);
        List<InspectionRoom> inspectionRoomsGotPenalty = inspectionRoomRepository.findAllByPostIdAndNotInPassState(postId, penalty0passStates);

        // 파일 이름 설정
        residentExcelParser.createAndRespondResidentInfoWithInspectionRoom(filename, sheetName, response, inspectionRoomsGotPenalty);
    }

    // (엑셀 다운로드) 방호점 게시글의 특정 통과차수에 해당하는 입사생 목록
    @GetMapping("/posts/inspection/{postId}/xlsx-residents")
    public void handleInspectionRoomExcelDownload(
            @PathVariable Long postId, HttpServletResponse response
            , @RequestParam(defaultValue = "NOT_PASSED") InspectionRoom.PassState passState
    ) {
        String postTitle = inspectionPostService.getById(postId).getTitle();

        String passStateValue = passState.getLabel();
        String filename = postTitle + " - " + passStateValue + " 명단" + "." + FileExt.XLSX.label;
        String sheetName = passStateValue + "명단";

        // N차 통과를 조회하려면 InspectionRoom까지 조회해야 함. Resident를 Inner Join해서 빈 방 조회를 피하자.
        // 2~10차 통과자 목록 조회, Room 정보까지 Fetch Join함.
        List<InspectionRoom> inspectionRooms = inspectionRoomRepository.findAllByPostIdAndPassState(postId, passState);

        // 파일 이름 설정
        residentExcelParser.createAndRespondResidentInfoWithInspectionRoom(filename, sheetName, response, inspectionRooms);
    }

    // (엑셀 다운로드) 방호점 게시글의 전체 데이터
    @GetMapping("/posts/inspection/{postId}/xlsx-all-data")
    public void handleExcelDownloadAll(
            @PathVariable Long postId, HttpServletResponse response
    ) {
        String postTitle = inspectionPostService.getById(postId).getTitle();

        String filename = postTitle + " - 점검표 전체 데이터" + "." + FileExt.XLSX.label;
        String sheetName = "점검표 전체 데이터";

        // N차 통과를 조회하려면 InspectionRoom까지 조회해야 함. Resident를 Inner Join해서 빈 방 조회를 피하자.
        List<InspectionRoom> inspectionRoomsGotPenalty = inspectionRoomRepository.findAllByPostId(postId);

        // 파일 이름 설정
        residentExcelParser.createAndRespondAllDataWithInspectionRoom(filename, sheetName, response, inspectionRoomsGotPenalty);
    }
}
