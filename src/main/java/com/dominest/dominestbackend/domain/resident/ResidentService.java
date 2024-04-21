package com.dominest.dominestbackend.domain.resident;

import com.dominest.dominestbackend.api.resident.response.ExcelUploadResponse;
import com.dominest.dominestbackend.api.resident.response.PdfBulkUploadResponse;
import com.dominest.dominestbackend.api.resident.request.SaveResidentRequest;
import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.resident.component.ResidenceSemester;
import com.dominest.dominestbackend.domain.room.Room;
import com.dominest.dominestbackend.domain.room.RoomService;
import com.dominest.dominestbackend.domain.room.roomhistory.RoomHistoryService;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.business.BusinessException;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import com.dominest.dominestbackend.global.util.ExcelParser;
import com.dominest.dominestbackend.global.util.FileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ResidentService {
    private final ResidentExcelParser residentExcelParser;
    private final ResidentRepository residentRepository;
    private final FileManager fileManager;
    private final RoomService roomService;
    private final RoomHistoryService roomHistoryService;
    private final ResidentFileManager residentFileManager;

    /** return 저장한 파일명 */
    @Transactional
    public void uploadPdf(Long id, FileManager.FilePrefix filePrefix, MultipartFile file) {
        if (fileManager.isInvalidFileExtension(file.getOriginalFilename(), FileManager.FileExt.PDF)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_EXTENSION);
        }

        Resident resident = findById(id);
        String fileNameToUpload = resident.generatePdfFileNameToStore();

        fileManager.save(filePrefix, file, fileNameToUpload);

        String prevFilename = residentFileManager.getPdfFilename(resident, filePrefix);
        residentFileManager.setPdfFilenameToResident(resident, filePrefix, fileNameToUpload);

        if (prevFilename != null)
            fileManager.deleteFile(filePrefix, prevFilename);
    }

    @Transactional
    public PdfBulkUploadResponse uploadPdfs(FileManager.FilePrefix filePrefix, List<MultipartFile> files, ResidenceSemester residenceSemester) {
        PdfBulkUploadResponse response = new PdfBulkUploadResponse();
        for (MultipartFile file : files) {
            // 빈 객체면 continue
            if (file.isEmpty()) {
                continue;
            }

            String filename = file.getOriginalFilename();
            // pdf 확장자가 아니라면 continue
            if (fileManager.isInvalidFileExtension(filename, FileManager.FileExt.PDF)) {
                continue;
            }

            // 1. 파일명으로 해당 차수의 학생이름을 찾는다. 파일명은 '학생이름.file' 여야 한다.
            String residentName = fileManager.extractFileNameNoExt(filename);
            Resident resident = residentRepository.findByPersonalInfoNameAndResidenceSemester(residentName, residenceSemester);

            // 파일명에 해당하는 학생이 없으면 continue
            if (resident == null) {
                response.addToDtoList(filename, "FAILED", "학생명이 파일명과 일치하지 않습니다.");
                continue;
            }

            String fileNameToUpload = resident.generatePdfFileNameToStore();
            fileManager.save(filePrefix, file, fileNameToUpload);

            String prevFilename = residentFileManager.getPdfFilename(resident, filePrefix);
            residentFileManager.setPdfFilenameToResident(resident, filePrefix, fileNameToUpload);

            response.addToDtoList(filename, "OK", null);
            response.addSuccessCount();

            if (prevFilename != null)
                fileManager.deleteFile(filePrefix, prevFilename);
        }
        // 한 건도 업로드하지 못했으면 예외발생
        if (response.getSuccessCount() == 0)
            throw new BusinessException(ErrorCode.NO_FILE_UPLOADED);
        return response;
    }

    @Transactional
    public ExcelUploadResponse excelUpload(List<List<String>> sheet, ResidenceSemester residenceSemester) {
        residentExcelParser.validateResidentColumnCount(sheet);
        // 첫 3줄 제거 후 유효 데이터만 추출
        sheet.remove(0); sheet.remove(0);sheet.remove(0);

        int originalRow = sheet.size();
        int successRow = 0;

        // 데이터를 저장한다. 예외발생시 삭제나 저장 작업의 트랜잭션 롤백.
        for (List<String> row : sheet) {
            if ("".equals(row.get(ResidentExcelParser.RESIDENT_COLUMN_COUNT - 1))) // 빈 row 발견 시 continue
                continue;
            // Room 객체를 찾아서 넣어줘야 함
            String assignedRoom = row.get(11);

            Room room = roomService.getByAssignedRoom(assignedRoom);
            Resident resident = Resident.from(row, residenceSemester, room);

            // 중복을 검사함. 같은 사람이라고 판단될 경우와 동명이인이라고 판단될 경우에 따라 분기.
            if (residentRepository.existsByPersonalInfoNameAndResidenceSemester(resident.getPersonalInfo().getName(), residenceSemester)) {
                if (existsByUniqueKey(resident)) {
                    // 엑셀 데이터상 중복이 있을 시 로그만 남기고 다음 행으로 넘어간다.
                    log.warn("엑셀 데이터 저장 실패. 중복 데이터가 있어 다음으로 넘어감. 이름: {}, 학번: {}, 학기: {}" +
                                    ", 방 번호: {}, 방 코드: {}", resident.getPersonalInfo().getName()
                            , resident.getStudentInfo().getStudentId(), resident.getResidenceSemester()
                            , resident.getRoom().getId(), resident.getRoom().getAssignedRoom());
                    continue;
                } else {
                    // 동명이인일 경우 이름 바꿔서 저장
                    resident.changeNameWithPhoneNumber();
                }
            }
            // save()에서 {방-학기}, {이름-학기} 등이 중복될 경우는 예외 던지고 롤백한다..
            save(resident);
            successRow++;
        }
        return ExcelUploadResponse.of(originalRow, successRow);
    }

    public List<Resident> getAllResidentByResidenceSemesterFetchRoom(ResidenceSemester residenceSemester) {
        return residentRepository.findAllByResidenceSemesterFetchRoom(residenceSemester);
    }

    // 테스트용 전체삭제 API
    @Transactional
    public void deleteAllResident() {
        residentRepository.deleteAllInBatch();
    }

    // 단건 등록용
    @Transactional
    public void save(SaveResidentRequest request) {
        Room room = roomService.getByAssignedRoom(request.getAssignedRoom());
        Resident resident = request.toEntity(room);

        save(resident);
    }

    @Transactional
    public void updateResident(Long id, SaveResidentRequest request) {
        Room room = roomService.getByAssignedRoom(request.getAssignedRoom());
        Resident resident = request.toEntity(room);

        Resident residentToUpdate = findById(id);
        residentToUpdate.updateValueFrom(resident);

        try {
            residentRepository.saveAndFlush(residentToUpdate);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("입사생 정보 변경 실패, 잘못된 입력값입니다. 데이터 누락 혹은 중복을 확인해주세요." +
                    " 지정 학기에 같은 학번을 가졌거나, 같은 방을 사용중인 입사생이 있을 수 있습니다.", HttpStatus.BAD_REQUEST, e);
        }
        updateRoomHistory(residentToUpdate);
    }

    public Resident findById(Long id) {
        return residentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.RESIDENT, id));
    }

    @Transactional
    public void deleteById(Long id) {
        Resident resident = findById(id);
        residentRepository.delete(resident);
    }

    public List<Resident> findAllByResidenceSemester(ResidenceSemester semester) {
        return residentRepository.findAllByResidenceSemester(semester);
    }

    private boolean existsByUniqueKey(Resident resident) {
        return residentRepository.existsByResidenceSemesterAndStudentInfoStudentIdAndPersonalInfoPhoneNumberValueAndPersonalInfoName(
                resident.getResidenceSemester()
                , resident.getStudentInfo().getStudentId()
                , resident.getPersonalInfo().getPhoneNumber().getValue()
                , resident.getPersonalInfo().getName());
    }

    private void save(Resident resident) {
        try {
            // InspectionRoom 등 Resident를 참조하는 테이블에 결과를 반영하지 않는다.
            residentRepository.saveAndFlush(resident);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(
                    String.format("입사생 저장 실패, 잘못된 입력값입니다. 데이터 누락 혹은 중복을 확인해주세요. 이름: %s, 학번: %s, 학기: %s, 방 번호: %d, 방 코드: %s"
                            , resident.getPersonalInfo().getName(), resident.getStudentInfo().getStudentId(), resident.getResidenceSemester()
                            , resident.getRoom().getId(), resident.getRoom().getAssignedRoom())
                    , HttpStatus.BAD_REQUEST, e);
        }
        updateRoomHistory(resident);
    }

    private void updateRoomHistory(Resident resident) {
        roomHistoryService.saveFrom(resident);
    }
}
