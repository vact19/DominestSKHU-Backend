package com.dominest.dominestbackend.domain.post.inspection.service;

import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.post.common.RecentPost;
import com.dominest.dominestbackend.domain.post.common.RecentPostService;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.service.CategoryService;
import com.dominest.dominestbackend.domain.post.inspection.repository.InspectionPostRepository;
import com.dominest.dominestbackend.domain.post.inspection.entity.InspectionPost;
import com.dominest.dominestbackend.domain.post.inspection.floor.entity.InspectionFloor;
import com.dominest.dominestbackend.domain.post.inspection.floor.service.InspectionFloorService;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.entity.InspectionRoom;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.service.InspectionRoomService;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.component.InspectionResidentInfo;
import com.dominest.dominestbackend.domain.resident.repository.ResidentRepository;
import com.dominest.dominestbackend.domain.resident.entity.component.ResidenceSemester;
import com.dominest.dominestbackend.domain.room.entity.Room;
import com.dominest.dominestbackend.domain.room.repository.RoomRepository;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.service.UserService;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class InspectionPostService {
    private final RoomRepository roomRepository;
    private final InspectionPostRepository inspectionPostRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final InspectionRoomService inspectionRoomService;
    private final InspectionFloorService inspectionFloorService;
    private final ResidentRepository residentRepository;
    private final RecentPostService recentPostService;

    public InspectionPost getById(Long id) {
        return inspectionPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.INSPECTION_POST, id));
    }
    /**
     * 방역점검 게시글 생성한다.
     * 게시글만 생성하는 것이 아니라, 게시글에 연관된 각 층수(Floor 객체),
     * 각 층수에 연관된 InspectionRoom 객체도 생성한다.
     * DB에 많은 데이터를 삽입하는 작업.
     *
     * @return 생성된 게시글의 id
     */
    @Transactional
    public long create(@NotNull(message = "학기를 선택해주세요.") ResidenceSemester residenceSemester, Long categoryId, String email) {
        // InspectionPost 연관 객체인 category, user 찾기
        User user = userService.getUserByEmail(email);
        Category category = categoryService.validateCategoryType(
                categoryId, Type.INSPECTION);

        InspectionPost inspectionPost = InspectionPost.builder()
                .category(category)
                .writer(user)
                .residenceSemester(residenceSemester)
                .build();
        inspectionPostRepository.save(inspectionPost);

        // Floor 객체 생성
        final int START_FLOOR_NO = 2;
        final int END_FLOOR_NO = 10;
        List<InspectionFloor> inspectionFloors = new ArrayList<>();

        for (int i = START_FLOOR_NO; i <= END_FLOOR_NO; i++) {
            InspectionFloor inspectionFloor = InspectionFloor.builder()
                    .floorNumber(i)
                    .inspectionPost(inspectionPost)
                    .build();
            inspectionFloors.add(inspectionFloor);
        }

        inspectionFloors = inspectionFloorService.create(inspectionFloors);

        // InspectionRoom 객체 생성. 저장시 Room 객체와 Floor객체가 필요함.
        ArrayList<InspectionRoom> inspectionRooms = new ArrayList<>();
        for (InspectionFloor inspectionFloor : inspectionFloors) {
            Integer floorNumber = inspectionFloor.getFloorNumber();
            List<Room> rooms = roomRepository.findByFloorNo(floorNumber);
            for (Room room : rooms) { // InspectionRoom 은 Room 만큼 생성되어야 한다.
                InspectionResidentInfo inspectionResidentInfo = residentRepository.findByResidenceSemesterAndRoom(residenceSemester, room)
                        .map(InspectionResidentInfo::from)
                        .orElse(null);

                InspectionRoom inspectionRoom = InspectionRoom.builder()
                        .room(room)
                        .inspectionFloor(inspectionFloor)
                        .passState(InspectionRoom.PassState.NOT_PASSED)
                        .inspectionResidentInfo(inspectionResidentInfo) // null이든 아니든 그냥 저장.
                        .build();
                inspectionRooms.add(inspectionRoom);
            }
        }
        inspectionRoomService.create(inspectionRooms);

        RecentPost recentPost = RecentPost.builder()
                .title(inspectionPost.getTitle())
                .categoryLink(inspectionPost.getCategory().getPostsLink())
                .categoryType(inspectionPost.getCategory().getType())
                .link("/posts/inspection/" + inspectionPost.getId() + "/floors")
                .build();
        recentPostService.create(recentPost);

        return inspectionPost.getId();
    }

    @Transactional
    public void delete(Long postId) {
        InspectionPost post = getById(postId);
        inspectionPostRepository.delete(post);
    }

    @Transactional
    public long updateTitle(Long postId, String title) {
        InspectionPost inspectionPost = getById(postId);
        inspectionPost.setTitle(title);
        return postId;
    }

    public Page<InspectionPost> getPage(Long categoryId, Pageable pageable) {
        return inspectionPostRepository.findAllByCategory(categoryId, pageable);
    }

    public InspectionPost getByIdFetchCategory(Long id) {
        return inspectionPostRepository.findByIdFetchCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.INSPECTION_POST, id));
    }
}
