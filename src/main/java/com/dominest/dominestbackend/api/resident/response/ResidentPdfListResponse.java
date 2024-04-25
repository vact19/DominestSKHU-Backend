package com.dominest.dominestbackend.api.resident.response;

import com.dominest.dominestbackend.domain.resident.entity.Resident;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ResidentPdfListResponse {
    private List<PdfDto> pdfs;

    public static ResidentPdfListResponse from(List<Resident> residents) {
        List<PdfDto> pdfDtos = residents.stream()
                .map(PdfDto::new)
                .collect(Collectors.toList());
        return new ResidentPdfListResponse(pdfDtos);
    }

    @Getter
    private static class PdfDto {
        // 사용자 화면에 이름, 파일존재유무, 개별파일 조회 url
        long id;
        String residentName;
        String existsAdmissionFile;
        String existsDepartureFile;

        public PdfDto(Resident resident) {
            this.id = resident.getId();
            this.residentName = resident.getPersonalInfo().getName();
            this.existsAdmissionFile = resident.getResidenceInfo().getAdmissionPdfFileName() != null ? "성공" : "오류(파일없음)";
            this.existsDepartureFile = resident.getResidenceInfo().getDeparturePdfFileName() != null ? "성공" : "오류(파일없음)";
        }
    }
}
