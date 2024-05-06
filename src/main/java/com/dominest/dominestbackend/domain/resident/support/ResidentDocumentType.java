package com.dominest.dominestbackend.domain.resident.support;

import com.dominest.dominestbackend.domain.resident.entity.Resident;
import com.dominest.dominestbackend.global.util.FileManager;

public enum PdfType {
    ADMISSION, DEPARTURE // 입사신청서, 퇴사신청서
public enum ResidentDocumentType {
    ADMISSION // 입사신청서
    , DEPARTURE // 퇴사신청서
    ;
    public static ResidentDocumentType from(String documentType){
        return ResidentDocumentType.valueOf(documentType.toUpperCase());
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
