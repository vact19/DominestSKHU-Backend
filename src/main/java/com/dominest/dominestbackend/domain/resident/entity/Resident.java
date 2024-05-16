package com.dominest.dominestbackend.domain.resident.entity;

import com.dominest.dominestbackend.domain.common.jpa.BaseEntity;
import com.dominest.dominestbackend.domain.common.vo.PhoneNumber;
import com.dominest.dominestbackend.domain.resident.support.ResidentExcelParser;
import com.dominest.dominestbackend.domain.resident.entity.component.ResidenceSemester;
import com.dominest.dominestbackend.domain.room.entity.Room;
import com.dominest.dominestbackend.global.util.DatePatternParser;
import com.dominest.dominestbackend.global.util.UuidHolder;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = {
        // unique 제약에서 '학기' 는 기본적으로 깔고 간다.
        // 1. [학번, 전화번호, 이름] 중복제한:  똑같은 학생이 한 학기에 둘 이상 있을 순 없다.
        // 2. [방번호]가 중복되면 안된다. 학기중 하나의 방, 하나의 구역에 둘 이상이 있을 순 없다.
        // 3. [이름] 이 학기마다 중복되면 안된다. 사생 서류 검색관련 로직 때문에 이름+학기가 Unique해야 함.
        @UniqueConstraint(name = "unique_for_resident_info",
                                            columnNames = { "residenceSemester", "studentId", "phone_number"})
        , @UniqueConstraint(name = "unique_for_room",
                                            columnNames = { "room_id", "residenceSemester" })
        , @UniqueConstraint(name = "unique_for_document",
                                            columnNames = { "name", "residenceSemester" })
})
public class Resident extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private PersonalInfo personalInfo;
    @Embedded
    private StudentInfo studentInfo;
    @Embedded
    private ResidenceDateInfo residenceDateInfo;
    @Embedded
    private ResidenceInfo residenceInfo;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ResidenceSemester residenceSemester; // 거주학기. '2020-2' 와 같음

    // 방 정보는 하나지만 학생데이터는 학기마다 추가됨. N : 1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Builder(access = AccessLevel.PRIVATE)
    public Resident(
            PersonalInfo personalInfo, StudentInfo studentInfo
            , ResidenceDateInfo residenceDateInfo, ResidenceInfo residenceInfo
            , ResidenceSemester residenceSemester, Room room
    ) {
        this.personalInfo = personalInfo;
        this.studentInfo = studentInfo;
        this.residenceDateInfo = residenceDateInfo;
        this.residenceInfo = residenceInfo;

        this.residenceSemester = residenceSemester;
        this.room = room;
    }

    public static Resident from(ResidentExcelParser.ResidentCreationDto data, ResidenceSemester residenceSemester, Room room) {
        String yyyyMMdd = "yyyyMMdd";

        // create the resident object using builder
        return Resident.builder()
                .personalInfo(
                        new PersonalInfo(
                                data.getName()
                                , data.getGender()
                                , new PhoneNumber(data.getPhoneNumber())
                                , DatePatternParser.parseyyMMddToLocalDate(LocalDate.now().getYear(), data.getDateOfBirth())
                        ))
                .studentInfo(
                        new StudentInfo(
                                data.getStudentId()
                                , data.getMajor()
                                , data.getGrade()
                        ))
                .residenceDateInfo(
                        new ResidenceDateInfo(
                                LocalDate.parse(data.getAdmissionDate(), DateTimeFormatter.ofPattern(yyyyMMdd))
                                , "".equals(data.getLeavingDate()) ?  null :
                                LocalDate.parse(data.getLeavingDate(), DateTimeFormatter.ofPattern(yyyyMMdd))
                                , LocalDate.parse(data.getSemesterStartDate(), DateTimeFormatter.ofPattern(yyyyMMdd))
                                , LocalDate.parse(data.getSemesterEndDate(), DateTimeFormatter.ofPattern(yyyyMMdd))
                        ))
                .residenceInfo(
                        new ResidenceInfo(
                                data.getSemester()
                                , data.getCurrentStatus()
                                , data.getPeriod()
                                , data.getSocialCode()
                                , data.getSocialName()
                                , data.getFamilyHomeZipCode()
                                , data.getFamilyHomeAddress()
                        ))
                .residenceSemester(residenceSemester)
                .room(room)
                .build();
    }

    // 파라미터로 받은 entity의 값을 모두 복사해서 업데이트한다.
    public void updateValueFrom(Resident resident) {
        this.personalInfo = resident.getPersonalInfo();
        this.studentInfo = resident.getStudentInfo();
        this.residenceDateInfo = resident.getResidenceDateInfo();
        this.residenceInfo = resident.getResidenceInfo();

        this.residenceSemester = resident.getResidenceSemester();
        this.room = resident.getRoom();
    }

    // 이름 뒤에 전화번호 뒷자리를 붙여 변경한다. 중복을 피하기 위해 사용할 수 있음
    public void changeNameWithPhoneNumber() {
        String[] splitNumber = personalInfo.phoneNumber.getValue().split("-");
        if (splitNumber.length != 3)
            throw new IllegalArgumentException("전화번호 형식이 잘못되었습니다.");
        String lastFourDigits = splitNumber[splitNumber.length - 1]; // 마지막 4자리 숫자
        this.personalInfo.name = personalInfo.name + "(" + lastFourDigits + ")";
    }

    public String generateFileNameToStore(String fileExt, UuidHolder uuidHolder) {
        return this.personalInfo.name + "-" + uuidHolder.random() + "." + fileExt;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class PersonalInfo {
        @Column(nullable = false, length = 30)
        private String name;
        @Column(nullable = false)
        private String gender; // 현재 'M' or 'F' 인데 확장성을 위해 String 쓰기로 함
        @Embedded
        private PhoneNumber phoneNumber; // '010-1234-5678' 형식으로 저장
        // 엑셀데이터는 6자리로 저장되긴 하는데, 날짜 필터링 걸려면 날짜타입 사용해야 할 듯
        @Column(nullable = false)
        private LocalDate dateOfBirth;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class StudentInfo {
        @Column(nullable = false, length = 50)
        private String studentId;
        @Column(nullable = false)
        private String major; // 전공. 매학년 바뀔 수도 있으니 enum 사용하지 않는 걸로
        @Column(nullable = false)
        private String grade; // '3학년' 과 같은 식으로 저장된다
    }

    /**
     * 날짜정보(입퇴사, 학기)
     * */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class ResidenceDateInfo {
        // 아래의
        private LocalDate admissionDate; // 입사일.
        @Column(nullable = true)
        private LocalDate leavingDate; // 퇴사일
        private LocalDate semesterStartDate; // 학기시작일
        private LocalDate semesterEndDate; // 학기종료일
    }

    // 기숙사 입주 관련 정보
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @EqualsAndHashCode
    @Embeddable
    public static class ResidenceInfo {
        /** 기숙사정보 */
        @Column(nullable = false)
        private String semester; // 차수. '2023SMSK02' 형식
        @Column(nullable = false)
        private String currentStatus;  // 현재상태
        private String period; // 기간. 'LY' 'AY' 'VY' 'YY'
        private String socialCode; // 사회코드
        private String socialName; // 사회명

        private String familyHomeZipCode;
        private String familyHomeAddress;

        @Column(nullable = true)
        @Setter
        private String admissionFileName;
        @Column(nullable = true)
        @Setter
        private String departureFileName;

        public ResidenceInfo(
                String semester, String currentStatus, String period, String socialCode, String socialName
                , String familyHomeZipCode, String familyHomeAddress
        ) {
            this.semester = semester;
            this.currentStatus = currentStatus;
            this.period = period;
            this.socialCode = socialCode;
            this.socialName = socialName;
            this.familyHomeZipCode = familyHomeZipCode;
            this.familyHomeAddress = familyHomeAddress;

            admissionFileName = null;
            departureFileName = null;
        }
    }
}
