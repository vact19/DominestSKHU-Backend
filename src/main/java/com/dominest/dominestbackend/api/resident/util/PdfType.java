package com.dominest.dominestbackend.api.resident.util;

import com.dominest.dominestbackend.domain.resident.entity.Resident;
import com.dominest.dominestbackend.global.util.FileManager;

public enum PdfType {
    ADMISSION, DEPARTURE // 입사신청서, 퇴사신청서
    ;
    public static PdfType from(String pdfType){
        return PdfType.valueOf(pdfType.toUpperCase());
    }

    public FileManager.FilePrefix toFilePrefix(){
        if (this.equals(ADMISSION)){
            return FileManager.FilePrefix.RESIDENT_ADMISSION;
        }
        return FileManager.FilePrefix.RESIDENT_DEPARTURE;
    }

    public String getPdfFileName(Resident resident){
        if (this.equals(ADMISSION)){
            return resident.getResidenceInfo().getAdmissionPdfFileName();
        }
        return resident.getResidenceInfo().getDeparturePdfFileName();
    }
}
