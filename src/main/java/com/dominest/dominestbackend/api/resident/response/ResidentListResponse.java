package com.dominest.dominestbackend.api.resident.response;

import com.dominest.dominestbackend.domain.resident.Resident;
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
        private Integer roomNumber;
        private String assignedRoom;
        private LocalDate admissionDate;
        private String leavingDate; // null일 경우 빈 칸으로 반환하기 위해 String 사용
        private LocalDate semesterStartDate;
        private LocalDate semesterEndDate;
        private String phoneNumber;
        private String socialCode;
        private String socialName;
        private String zipCode;
        private String address;

        //from
        public static ResidentDto from(Resident resident){
            return ResidentDto.builder()
                    .id(resident.getId())
                    .name(resident.getName())
                    .gender(resident.getGender())
                    .studentId(resident.getStudentId())
                    .semester(resident.getSemester())
                    .currentStatus(resident.getCurrentStatus())
                    .dateOfBirth(resident.getDateOfBirth())
                    .dormitory(resident.getRoom().getDormitory())
                    .major(resident.getMajor())
                    .grade(resident.getGrade())
                    .period(resident.getPeriod())
                    .roomNumber(resident.getRoom().getRoomNo())
                    .assignedRoom(resident.getRoom().getAssignedRoom())
                    .admissionDate(resident.getAdmissionDate())
                    .leavingDate(resident.getLeavingDate() == null ? "" :
                            resident.getLeavingDate().toString())
                    .semesterStartDate(resident.getSemesterStartDate())
                    .semesterEndDate(resident.getSemesterEndDate())
                    .phoneNumber(resident.getPhoneNumber())
                    .socialCode(resident.getSocialCode())
                    .socialName(resident.getSocialName())
                    .zipCode(resident.getZipCode())
                    .address(resident.getAddress())
                    .build();
        }
    }
}

