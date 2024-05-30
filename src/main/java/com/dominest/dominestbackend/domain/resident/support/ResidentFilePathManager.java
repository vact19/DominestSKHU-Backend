package com.dominest.dominestbackend.domain.resident.support;

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
    public void setFilenameToResident(Resident resident, FileManager.FilePrefix filePrefix, String uploadedFilename) {
        switch (filePrefix) {
            case RESIDENT_ADMISSION:
                resident.getResidenceInfo().setAdmissionFileName(uploadedFilename);
                break;
            case RESIDENT_DEPARTURE:
                resident.getResidenceInfo().setDepartureFileName(uploadedFilename);
                break;
            default:
                throw new IllegalArgumentException("파일 경로 정보가 없어 저장 실패." +
                        " filePrefix value -> " + filePrefix.name());
        }
    }

    // FilePrefix (파일 타입) 에 맞는 파일명을 가져온다.
    public String getFilename(Resident resident, FileManager.FilePrefix filePrefix) {
        switch (filePrefix) {
            case RESIDENT_ADMISSION:
                return resident.getResidenceInfo().getAdmissionFileName();
            case RESIDENT_DEPARTURE:
                return resident.getResidenceInfo().getDepartureFileName();
            default:
                throw new IllegalArgumentException("파일 경로 정보가 없어 파일명 반환 실패." +
                        " filePrefix value -> "+ filePrefix.name());
        }
    }
}
