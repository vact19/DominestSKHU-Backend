package com.dominest.dominestbackend.domain.resident;

import com.dominest.dominestbackend.domain.resident.entity.Resident;
import com.dominest.dominestbackend.global.exception.exceptions.business.BusinessException;
import com.dominest.dominestbackend.global.util.FileManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.dominest.dominestbackend.global.util.FileManager.FilePrefix.RESIDENT_ADMISSION;
import static com.dominest.dominestbackend.global.util.FileManager.FilePrefix.RESIDENT_DEPARTURE;

/**
 * Resident와 File 관련 로직을 중개하는 클래스
 */
@Service
public class ResidentFilePathManager {
    // FilePrefix (파일 타입) 에 맞게 파일명을 등록한다.
    public void setPdfFilenameToResident(Resident resident, FileManager.FilePrefix filePrefix, String uploadedFilename) {
        if (filePrefix.equals(RESIDENT_ADMISSION)) {
            resident.getResidenceInfo().setAdmissionPdfFileName(uploadedFilename);
        } else if (filePrefix.equals(RESIDENT_DEPARTURE)) {
            resident.getResidenceInfo().setDeparturePdfFileName(uploadedFilename);
        } else { // 입사신청서, 퇴사신청서가 아닌 다른 FilePrefix 값일 때
            throw new BusinessException("잘못된 FilePrefix 값입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FilePrefix (파일 타입) 에 맞는 파일명을 가져온다.
    public String getPdfFilename(Resident resident, FileManager.FilePrefix filePrefix) {
        if (filePrefix.equals(RESIDENT_ADMISSION)) {
            return resident.getResidenceInfo().getAdmissionPdfFileName();
        } else if (filePrefix.equals(RESIDENT_DEPARTURE)) {
            return resident.getResidenceInfo().getDeparturePdfFileName();
        } else {
            return null;
        }
    }
}
