package com.dominest.dominestbackend.global.util;


import com.dominest.dominestbackend.domain.common.vo.Email;
import com.dominest.dominestbackend.domain.common.vo.PhoneNumber;
import com.dominest.dominestbackend.domain.post.cardkey.repository.CardKeyRepository;
import com.dominest.dominestbackend.domain.post.complaint.repository.ComplaintRepository;
import com.dominest.dominestbackend.domain.post.component.category.entity.Category;
import com.dominest.dominestbackend.domain.post.component.category.component.Type;
import com.dominest.dominestbackend.domain.post.component.category.repository.CategoryRepository;
import com.dominest.dominestbackend.domain.post.image.repository.ImageTypeRepository;
import com.dominest.dominestbackend.domain.post.manual.repository.ManualPostRepository;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.repository.UndeliveredParcelPostRepository;
import com.dominest.dominestbackend.domain.post.undeliveredparcelpost.component.repository.UndeliveredParcelRepository;
import com.dominest.dominestbackend.domain.room.entity.Room;
import com.dominest.dominestbackend.domain.room.repository.RoomRepository;
import com.dominest.dominestbackend.domain.schedule.entity.Schedule;
import com.dominest.dominestbackend.domain.schedule.repository.ScheduleRepository;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.component.Role;
import com.dominest.dominestbackend.domain.user.repository.UserJpaRepository;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Profile({
//        "local",
        "dev"
})
@Component
public class InitDB {
    private final PasswordEncoder passwordEncoder;
    private final UserJpaRepository userJpaRepository;
    private final CategoryRepository categoryRepository;
    private final RoomRepository roomRepository;
    private final List<InitUser> initUsers;
    private final int INIT_USER_CNT = 8;
    private final UndeliveredParcelPostRepository undelivParcelPostRepository;
    private final UndeliveredParcelRepository undelivParcelRepository;
    private final ImageTypeRepository imageTypeRepository;
    private final ComplaintRepository complaintRepository;
    private final CardKeyRepository cardKeyRepository;
    private final ScheduleRepository scheduleRepository;

    private final ManualPostRepository manualPostRepository;


    @Autowired
    public InitDB(PasswordEncoder passwordEncoder, UserJpaRepository userJpaRepository
            , CategoryRepository categoryRepository, RoomRepository roomRepository
            , @Value("${init.user1.email}") String email1, @Value("${init.user1.pwd}") String pwd1, @Value("${init.user1.name}") String name1, @Value("${init.user1.phone}") String phone1, @Value("${init.user1.role}") Role role1
            , @Value("${init.user2.email}") String email2, @Value("${init.user2.pwd}") String pwd2, @Value("${init.user2.name}") String name2, @Value("${init.user2.phone}") String phone2, @Value("${init.user2.role}") Role role2
            , @Value("${init.user3.email}") String email3, @Value("${init.user3.pwd}") String pwd3, @Value("${init.user3.name}") String name3, @Value("${init.user3.phone}") String phone3, @Value("${init.user3.role}") Role role3
            , @Value("${init.user4.email}") String email4, @Value("${init.user4.pwd}") String pwd4, @Value("${init.user4.name}") String name4, @Value("${init.user4.phone}") String phone4, @Value("${init.user4.role}") Role role4
            , @Value("${init.user5.email}") String email5, @Value("${init.user5.pwd}") String pwd5, @Value("${init.user5.name}") String name5, @Value("${init.user5.phone}") String phone5, @Value("${init.user5.role}") Role role5
            , @Value("${init.user6.email}") String email6, @Value("${init.user6.pwd}") String pwd6, @Value("${init.user6.name}") String name6, @Value("${init.user6.phone}") String phone6, @Value("${init.user6.role}") Role role6
            , @Value("${init.user7.email}") String email7, @Value("${init.user7.pwd}") String pwd7, @Value("${init.user7.name}") String name7, @Value("${init.user7.phone}") String phone7, @Value("${init.user7.role}") Role role7
            , @Value("${init.user8.email}") String email8, @Value("${init.user8.pwd}") String pwd8, @Value("${init.user8.name}") String name8, @Value("${init.user8.phone}") String phone8, @Value("${init.user8.role}") Role role8,
                  UndeliveredParcelPostRepository undeliveredParcelPostRepository, UndeliveredParcelRepository undeliveredParcelRepository, ImageTypeRepository imageTypeRepository, ComplaintRepository complaintRepository, CardKeyRepository cardKeyRepository, ScheduleRepository scheduleRepository, ManualPostRepository manualPostRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userJpaRepository = userJpaRepository;
        this.categoryRepository = categoryRepository;
        this.roomRepository = roomRepository;

        List<InitUser> initUsers = new ArrayList<>();

        InitUser user1 = InitUser.builder().email(email1).password(pwd1).name(name1).phoneNumber(phone1).role(role1).build();
        InitUser user2 = InitUser.builder().email(email2).password(pwd2).name(name2).phoneNumber(phone2).role(role2).build();
        InitUser user3 = InitUser.builder().email(email3).password(pwd3).name(name3).phoneNumber(phone3).role(role3).build();
        InitUser user4 = InitUser.builder().email(email4).password(pwd4).name(name4).phoneNumber(phone4).role(role4).build();
        InitUser user5 = InitUser.builder().email(email5).password(pwd5).name(name5).phoneNumber(phone5).role(role5).build();
        InitUser user6 = InitUser.builder().email(email6).password(pwd6).name(name6).phoneNumber(phone6).role(role6).build();
        InitUser user7 = InitUser.builder().email(email7).password(pwd7).name(name7).phoneNumber(phone7).role(role7).build();
        InitUser user8 = InitUser.builder().email(email8).password(pwd8).name(name8).phoneNumber(phone8).role(role8).build();

        initUsers.add(user1); initUsers.add(user2); initUsers.add(user3); initUsers.add(user4);
        initUsers.add(user5); initUsers.add(user6); initUsers.add(user7); initUsers.add(user8);

        this.initUsers = initUsers;
        this.undelivParcelPostRepository = undeliveredParcelPostRepository;
        this.undelivParcelRepository = undeliveredParcelRepository;
        this.imageTypeRepository = imageTypeRepository;
        this.complaintRepository = complaintRepository;
        this.cardKeyRepository = cardKeyRepository;
        this.scheduleRepository = scheduleRepository;
        this.manualPostRepository = manualPostRepository;
    }

    @Getter
    @Builder
    static class InitUser {
        String email;
        String password;
        String name;
        String phoneNumber;
        Role role;
    }

    @Transactional
    @PostConstruct
    public void init() {
        ArrayList<User> users = new ArrayList<>();

        for (int i = 0; i < INIT_USER_CNT; i++) {
            InitUser initUser = initUsers.get(i);
            User user = User.builder()
                    .email(new Email(initUser.getEmail()))
                    .password(passwordEncoder.encode(initUser.getPassword()))
                    .name(initUser.getName())
                    .phoneNumber(new PhoneNumber(initUser.getPhoneNumber()))
                    .role(initUser.getRole())
                    .build();
            users.add(user);
        }
        userJpaRepository.saveAll(users);

        Category undelivCategoryNo1 = Category.builder()
                .name("장기 미수령 택배 관리대장")
                .type(Type.UNDELIVERED_PARCEL_REGISTER)
                .explanation("장기 미수령 택배 관리대장")
                .orderKey(1)
                .build();
        categoryRepository.save(undelivCategoryNo1);

        Category complaintCategoryNO2 = Category.builder()
                .name("민원접수내역")
                .type(Type.COMPLAINT)
                .explanation("민원접수내역")
                .orderKey(2)
                .build();
        categoryRepository.save(complaintCategoryNO2);

        Category cardKeyCategoryNO3 = Category.builder()
                .name("카드키 관리대장")
                .type(Type.CARD_KEY)
                .explanation("카드키 관리대장")
                .orderKey(3)
                .build();
        categoryRepository.save(cardKeyCategoryNO3);

        Category inspectionCategoryNO4 = Category.builder()
                .name("방역호실점검")
                .type(Type.INSPECTION)
                .explanation("방역호실점검")
                .orderKey(4)
                .build();
        categoryRepository.save(inspectionCategoryNO4);

        Category imageCategoryNo5 = Category.builder()
                .name("사진 업로드")
                .type(Type.IMAGE)
                .explanation("사진 업로드")
                .orderKey(5)
                .build();
        categoryRepository.save(imageCategoryNo5);

        Category manualCategoryNo6 = Category.builder()
                .name("사용 설명서")
                .type(Type.MANUAL)
                .explanation("사용 설명서")
                .orderKey(6)
                .build();
        categoryRepository.save(manualCategoryNo6);

        ArrayList<Schedule> schedules = new ArrayList<>();
        for (Schedule.DayOfWeek dayOfWeek : Schedule.DayOfWeek.values()) {
            for (Schedule.TimeSlot timeSlot : Schedule.TimeSlot.values()) {
                schedules.add(Schedule.of(dayOfWeek, timeSlot));
            }
        }
        scheduleRepository.saveAll(schedules);

        
//        UndeliveredParcelPost unDeliParcelPost = UndeliveredParcelPost.builder()
//                .category(undelivCategoryNo1)
//                .writer(firstUser)
//                .build();
//        undelivParcelPostRepository.save(unDeliParcelPost);
//
//        UndeliveredParcel parcel = UndeliveredParcel.builder()
//                .recipientName("받는사람")
//                .recipientPhoneNum("010-1234-5678")
//                .instruction("배송지시사항")
//                .processState(UndeliveredParcel.ProcessState.MESSAGE_SENT)
//                .post(unDeliParcelPost)
//                .build();
//        UndeliveredParcel parcel2 = UndeliveredParcel.builder()
//                .recipientName("받는사람")
//                .recipientPhoneNum("010-1234-5678")
//                .instruction("배송지시사항")
//                .processState(UndeliveredParcel.ProcessState.MESSAGE_SENT)
//                .post(unDeliParcelPost)
//                .build();
//        undelivParcelRepository.save(parcel);
//        undelivParcelRepository.save(parcel2);
//
//
//
//        ArrayList<ImageType> imageTypes = new ArrayList<>();
//        int postCount = 100;
//        for (int i = 1; i <= postCount; i++) {
//            ImageType imageType = ImageType.builder()
//                    .title("title" + i)
//                    .writer(firstUser)
//                    .category(imageCategoryNo5) // 3번째 카테고리
//                    .build();
//            imageTypes.add(imageType);
//        }
//        imageTypeRepository.saveAll(imageTypes);
//
//        ArrayList<Complaint> complaints = new ArrayList<>();
//        int complaintCount = 23;
//        for (int i = 1; i <= complaintCount; i++) {
//            Complaint complaint = Complaint.builder()
//                    .name("고세구먼트" + i)
//                    .roomNo("101")
//                    .complaintCause("난방 불가")
//                    .complaintResolution("난방 수으리 완무료")
//                    .processState(Complaint.ProcessState.PROCESSING)
//                    .date(LocalDate.now())
//                    .writer(firstUser)
//                    .category(complaintCategoryNO2) // 민원접수내역
//                    .build();
//            complaints.add(complaint);
//        }
//        complaintRepository.saveAll(complaints);
//
//        ArrayList<CardKey> cardKeys = new ArrayList<>();
//        int cardKeyCount = 23;
//        for (int i = 1; i <= cardKeyCount; i++) {
//            CardKey cardKey = CardKey.builder()
//                    .issuedDate(LocalDate.now())
//                    .roomNo("10" + i)
//                    .name("송승헌" + i)
//                    .dateOfBirth(LocalDate.of(1999, 1, i))
//                    .reIssueCnt(i)
//                    .etc(i + "번 안아줘요")
//                    .writer(firstUser)
//                    .category(cardKeyCategoryNO3)
//                    .build();
//            cardKeys.add(cardKey);
//        }
//        cardKeyRepository.saveAll(cardKeys);

        // 2~3층은 26호실까지, 4~10층은 17호실까지 있음
        List<Room> rooms = createRooms();

        roomRepository.saveAll(rooms);
    }

    private static List<Room> createRooms() {
        int roomAmount2To3 = 26; // 2~3층에는 26개의 방이 있음
        int roomAmount4To10 = 17; // 4~10층에는 17개의 방이 있음
        List<Room> rooms = new ArrayList<>();

        // 2층과 3층에 대한 방 추가
        for (int floor = 2; floor <= 3; floor += 1) {
            rooms.addAll(
                    createRoomsFor(roomAmount2To3, String.format("%02d", floor))
            );
        }
        // 4층부터 10층까지 방 추가
        for (int floor = 4; floor <= 10; floor++) {
            rooms.addAll(
                    createRoomsFor(roomAmount4To10, String.format("%02d", floor))
            );
        }
        return rooms;
    }

    // 애초에 빈 Room List를 넘겨서, 함수 내부에서 Room List를 변경하도록 하면 안됨. 빌려준걸 바꿔버리면 안돼
    private static List<Room> createRoomsFor(int roomAmount, String floor) {
        List<Room> roomsForFloor = new ArrayList<>();
        for (int i = 1; i <= roomAmount; i++) {
            String roomNo = String.format("%02d", i);
            int floorNo = Integer.parseInt(floor);
            StringBuilder sb = new StringBuilder();

            Room roomA = new Room(sb.append("B")
                    .append(floor)
                    .append(roomNo)
                    .append("A")
                    .toString()
                    , floorNo, Room.Dormitory.HAENGBOK);

            roomsForFloor.add(roomA);
            sb.setLength(0);

            Room roomB = new Room(sb
                            .append("B")
                            .append(floor)
                            .append(roomNo)
                            .append("B")
                            .toString()
                    , floorNo, Room.Dormitory.HAENGBOK);
            roomsForFloor.add(roomB);
        }
        return roomsForFloor;
    }
}
