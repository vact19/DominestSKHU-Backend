package com.dominest.dominestbackend.api.resident.response;

import com.dominest.dominestbackend.domain.resident.entity.Resident;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ResidentDocumentListResponse {
    private List<ResidentDocumentDto> documents;

    public static ResidentDocumentListResponse from(List<Resident> residents) {
        List<ResidentDocumentDto> residentDocumentDtos = residents.stream()
                .map(ResidentDocumentDto::new)
                .collect(Collectors.toList());
        return new ResidentDocumentListResponse(residentDocumentDtos);
    }

    @Getter
    private static class ResidentDocumentDto {
        // 사용자 화면에 이름, 파일존재유무, 개별파일 조회 url
        long id;
        String residentName;
        String existsAdmissionFile;
        String existsDepartureFile;

        public ResidentDocumentDto(Resident resident) {
            this.id = resident.getId();
            this.residentName = resident.getPersonalInfo().getName();
            this.existsAdmissionFile = resident.getResidenceInfo().getAdmissionFileName() != null ? "성공" : "오류(파일없음)";
            this.existsDepartureFile = resident.getResidenceInfo().getDepartureFileName() != null ? "성공" : "오류(파일없음)";
        }
    }
}
