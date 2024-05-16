package com.dominest.dominestbackend.api.resident.request;

import com.dominest.dominestbackend.domain.common.vo.PhoneNumber;
import com.dominest.dominestbackend.domain.resident.entity.Resident;
import com.dominest.dominestbackend.domain.resident.entity.component.ResidenceSemester;
import com.dominest.dominestbackend.domain.room.entity.Room;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class SaveResidentRequest {
    @NotBlank(message = "이름을 입력해주세요.")
    String name;
    @NotBlank(message = "성별을 입력해주세요.")
    String gender;
    @NotBlank(message = "학번을 입력해주세요.")
    String studentId;
    @NotBlank(message = "학과를 입력해주세요.")
    String major;
    @NotBlank(message = "학년을 입력해주세요.")
    String grade;
    @NotNull(message = "생년월일을 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyMMdd")
    LocalDate dateOfBirth;
    @NotBlank(message = "학기를 입력해주세요.")
    String semester;
    @NotNull(message = "거주학기를 입력해주세요.")
    ResidenceSemester residenceSemester;
    @NotBlank(message = "현재 상태를 입력해주세요.")
    String currentStatus;
    @NotBlank(message = "기숙사를 입력해주세요.")
    String dormitory;
    @NotBlank(message = "기간을 입력해주세요.")
    String period;
    @Min(value = 1, message = "양의 정수만 입력해주세요.")
    @NotNull(message = "방 번호를 입력해주세요.")
    Integer roomNumber;
    @NotBlank(message = "방 배정 여부를 입력해주세요.")
    String assignedRoom;
    @NotNull(message = "입사일을 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    LocalDate admissionDate;
    @NotNull(message = "퇴사일을 입력해주세요.")
    String leavingDate; // 빈 칸일 경우 null 처리하기 위해 String 사용
    @NotNull(message = "학기 시작일을 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    LocalDate semesterStartDate;
    @NotNull(message = "학기 종료일을 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    LocalDate semesterEndDate;
    @NotBlank(message = "전화번호를 입력해주세요.")
    String phoneNumber;
    @NotBlank(message = "사회코드를 입력해주세요.")
    String socialCode;
    @NotBlank(message = "사회명을 입력해주세요.")
    String socialName;
    @NotBlank(message = "우편번호를 입력해주세요.")
    String familyHomeZipCode;
    @NotBlank(message = "주소를 입력해주세요.")
    String familyHomeAddress;

    public Resident toEntity(Room room){
        Resident.PersonalInfo personalInfo = new Resident.PersonalInfo(
                name, gender, new PhoneNumber(phoneNumber), dateOfBirth
        );
        Resident.StudentInfo studentInfo = new Resident.StudentInfo(
                studentId
                , major
                , grade
        );
        Resident.ResidenceDateInfo dateInfo = new Resident.ResidenceDateInfo(
                admissionDate
                , "".equals(leavingDate) ? null :
                LocalDate.parse(leavingDate, DateTimeFormatter.ofPattern("yyyyMMdd"))
                , semesterStartDate
                , semesterEndDate
        );
        Resident.ResidenceInfo residenceInfo = new Resident.ResidenceInfo(
                semester
                , currentStatus
                , period
                , socialCode
                , socialName
                , familyHomeZipCode
                , familyHomeAddress
        );
        return new Resident(personalInfo, studentInfo, dateInfo, residenceInfo, residenceSemester, room);
    }
}
