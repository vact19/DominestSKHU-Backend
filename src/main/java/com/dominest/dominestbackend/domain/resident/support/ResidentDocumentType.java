package com.dominest.dominestbackend.domain.resident.support;

import com.dominest.dominestbackend.domain.resident.entity.Resident;
import com.dominest.dominestbackend.global.util.FileManager;

public enum ResidentDocumentType {
    ADMISSION // 입사신청서
    , DEPARTURE // 퇴사신청서
    ;
    public static ResidentDocumentType from(String documentType){
        return ResidentDocumentType.valueOf(documentType.toUpperCase());
    }

    public FileManager.FilePrefix toFilePrefix() {
        switch (this) {
            case ADMISSION:
                return FileManager.FilePrefix.RESIDENT_ADMISSION;
            case DEPARTURE:
                return FileManager.FilePrefix.RESIDENT_DEPARTURE;
            default:
                throw new IllegalArgumentException("Unexpected value: " + this.name());
        }
    }

    public String getDocumentFileName(Resident resident) {
        switch (this) {
            case ADMISSION:
                return resident.getResidenceInfo().getAdmissionFileName();
            case DEPARTURE:
                return resident.getResidenceInfo().getDepartureFileName();
            default:
                throw new IllegalArgumentException("Unexpected value: " + this.name());
        }
    }
}
