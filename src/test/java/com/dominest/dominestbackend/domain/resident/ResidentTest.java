package com.dominest.dominestbackend.domain.resident;

import com.dominest.dominestbackend.domain.common.vo.PhoneNumber;
import com.dominest.dominestbackend.domain.resident.entity.Resident;
import com.dominest.dominestbackend.domain.resident.entity.component.ResidenceSemester;
import com.dominest.dominestbackend.domain.room.entity.Room;
import com.dominest.dominestbackend.global.util.DatePatternParser;
import com.dominest.dominestbackend.global.util.FileManager;
import com.dominest.dominestbackend.global.util.TestUuidHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class ResidentTest {

    // changeNameWithPhoneNumber*, generateFileNameToStore*, updateValueFrom, 생성자 테스트해야함
    private final String phoneNumber = "010-1234-5678";
    private final String name = "홍길동";

    @DisplayName("기존 이름 뒤에 전화번호를 붙여 이름을 변경할 수 있다")
    @Test
    void constructor() {
        // given
        Resident resident = createResident();
        String residentName = resident.getPersonalInfo().getName();

        String[] splitPhoneNumber = resident.getPersonalInfo().getPhoneNumber().getValue().split("-");
        String lastFourDigits = splitPhoneNumber[splitPhoneNumber.length - 1];
        String changedName = residentName + "(" + lastFourDigits + ")";
        // when
        resident.changeNameWithPhoneNumber();
        // then
        assertThat(resident.getPersonalInfo().getName()).isEqualTo(changedName);
    }

    @DisplayName("이름과 UUID를 조합한 파일명을 생성할 수 있다")
    @Test
    void generateFileNameToStore() {
        // given
        Resident resident = createResident();
        String residentName = resident.getPersonalInfo().getName();
        String fileExt = FileManager.FileExt.XLSX.label;
        String testUuid = "test-uuid";

        String generatedFileName = residentName + "-" + testUuid + "." + fileExt;
        // when
        String result = resident.generateFileNameToStore(fileExt, new TestUuidHolder(testUuid));
        // then
        assertThat(result).isEqualTo(generatedFileName);
    }

    @DisplayName("Resident 엔티티의 값을 업데이트할 수 있다")
    @Test
    void updateValueFrom() {
        // given
        Resident resident = createResident();
        Resident.PersonalInfo personalInfo = new Resident.PersonalInfo(
                "newName", "newGender", new PhoneNumber("010-8765-4321"), LocalDate.of(2000, 1, 1)
        );
        Resident.StudentInfo studentInfo = new Resident.StudentInfo(
                "newStudentId"
                , "newMajor"
                , "newGrade"
        );
        Resident.ResidenceDateInfo dateInfo = new Resident.ResidenceDateInfo(
                LocalDate.of(2022, 1, 1)
                , LocalDate.of(2022, 1, 1)
                , LocalDate.of(2022, 3, 1)
                , LocalDate.of(2022, 7, 1)
        );
        Resident.ResidenceInfo residenceInfo = new Resident.ResidenceInfo(
                "newSemester"
                , "newCurrentStatus"
                , "newPeriod"
                , "newSocialCode"
                , "newSocialName"
                , "newFamilyHomeZipCode"
                , "newFamilyHomeAddress"
        );
        Room room = new Room("B1027A", 10, Room.Dormitory.HAENGBOK);
        Resident newResident = new Resident(personalInfo, studentInfo, dateInfo, residenceInfo
                , ResidenceSemester.S2024_1, room);
        // when
        resident.updateValueFrom(newResident);
        // then
        assertThat(resident.getPersonalInfo()).isEqualTo(newResident.getPersonalInfo());
        assertThat(resident.getStudentInfo()).isEqualTo(newResident.getStudentInfo());
        assertThat(resident.getResidenceDateInfo()).isEqualTo(newResident.getResidenceDateInfo());
        assertThat(resident.getResidenceInfo()).isEqualTo(newResident.getResidenceInfo());

        assertThat(resident.getResidenceSemester()).isEqualTo(ResidenceSemester.S2024_1);
        assertThat(resident.getRoom().getAssignedRoom()).isEqualTo(room.getAssignedRoom());
    }

    private Resident createResident() {
        Resident.PersonalInfo personalInfo = new Resident.PersonalInfo(
                name, "gender", new PhoneNumber(phoneNumber), LocalDate.of(2000, 1, 1)
        );
        Resident.StudentInfo studentInfo = new Resident.StudentInfo(
                "studentId"
                , "major"
                , "grade"
        );
        Resident.ResidenceDateInfo dateInfo = new Resident.ResidenceDateInfo(
                LocalDate.of(2021, 1, 1)
                , StringUtils.hasText("20220101") ? LocalDate.parse("20220101", DateTimeFormatter.ofPattern("yyyyMMdd"))
                    : null
                , LocalDate.of(2021, 3, 1)
                , LocalDate.of(2021, 7, 1)
        );
        Resident.ResidenceInfo residenceInfo = new Resident.ResidenceInfo(
                "semester"
                , "currentStatus"
                , "period"
                , "socialCode"
                , "socialName"
                , "familyHomeZipCode"
                , "familyHomeAddress"
        );
        Room room = new Room("B1027A", 10, Room.Dormitory.HAENGBOK);
        return new Resident(personalInfo, studentInfo, dateInfo, residenceInfo
                , ResidenceSemester.S2024_1, room);
    }
}
