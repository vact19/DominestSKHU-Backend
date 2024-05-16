package com.dominest.dominestbackend.api.resident.response;

import com.dominest.dominestbackend.domain.resident.entity.Resident;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ResidentListResponse {
    private List<ResidentDto> residents;

    public static ResidentListResponse from(List<Resident> residents){
        List<ResidentDto> residentDtos = residents.stream()
                .map(ResidentDto::from)
                .collect(Collectors.toList());
        return new ResidentListResponse(residentDtos);
    }

    @Getter
    @Builder
    private static class ResidentDto{
        private Long id;
        private String name;
        private String gender;
        private String studentId;
        private String major;
        private String grade;
        private LocalDate dateOfBirth;
        private String semester;
        private String currentStatus;
        private String dormitory;
        private String period;
        private int roomNumber;
        private String assignedRoom;
        private LocalDate admissionDate;
        private String leavingDate; // null일 경우 빈 칸으로 반환하기 위해 String 사용
        private LocalDate semesterStartDate;
        private LocalDate semesterEndDate;
        private String phoneNumber;
        private String socialCode;
        private String socialName;
        private String familyHomeZipCode;
        private String familyHomeAddress;

        //from
        public static ResidentDto from(Resident resident){
            Resident.PersonalInfo personalInfo = resident.getPersonalInfo();
            Resident.StudentInfo studentInfo = resident.getStudentInfo();
            Resident.ResidenceDateInfo residenceDateInfo = resident.getResidenceDateInfo();
            Resident.ResidenceInfo residenceInfo = resident.getResidenceInfo();
            return ResidentDto.builder()
                    .id(resident.getId())
                    .name(personalInfo.getName())
                    .gender(personalInfo.getGender())
                    .studentId(studentInfo.getStudentId())
                    .semester(residenceInfo.getSemester())
                    .currentStatus(residenceInfo.getCurrentStatus())
                    .dateOfBirth(personalInfo.getDateOfBirth())
                    .dormitory(resident.getRoom().getDormitory().dormitoryCode)
                    .major(studentInfo.getMajor())
                    .grade(studentInfo.getGrade())
                    .period(residenceInfo.getPeriod())
                    .roomNumber(resident.getRoom().getDormitory().roomNo)
                    .assignedRoom(resident.getRoom().getAssignedRoom())
                    .admissionDate(residenceDateInfo.getAdmissionDate())
                    .leavingDate(residenceDateInfo.getLeavingDate() == null ? "" :
                            residenceDateInfo.getLeavingDate().toString())
                    .semesterStartDate(residenceDateInfo.getSemesterStartDate())
                    .semesterEndDate(residenceDateInfo.getSemesterEndDate())
                    .phoneNumber(personalInfo.getPhoneNumber().getValue())
                    .socialCode(residenceInfo.getSocialCode())
                    .socialName(residenceInfo.getSocialName())
                    .familyHomeZipCode(residenceInfo.getFamilyHomeZipCode())
                    .familyHomeAddress(residenceInfo.getFamilyHomeAddress())
                    .build();
        }
    }
}
