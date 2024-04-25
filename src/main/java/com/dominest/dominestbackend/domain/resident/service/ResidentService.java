package com.dominest.dominestbackend.domain.resident.service;

import com.dominest.dominestbackend.api.resident.response.ExcelUploadResponse;
import com.dominest.dominestbackend.api.resident.response.PdfBulkUploadResponse;
import com.dominest.dominestbackend.api.resident.request.SaveResidentRequest;
import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.resident.support.ResidentExcelParser;
import com.dominest.dominestbackend.domain.resident.support.ResidentFilePathManager;
import com.dominest.dominestbackend.domain.resident.support.ResidentSearchMap;
import com.dominest.dominestbackend.domain.resident.entity.component.ResidenceSemester;
import com.dominest.dominestbackend.domain.resident.entity.Resident;
import com.dominest.dominestbackend.domain.resident.repository.ResidentRepository;
import com.dominest.dominestbackend.domain.room.entity.Room;
import com.dominest.dominestbackend.domain.room.repository.RoomRepository;
import com.dominest.dominestbackend.domain.room.support.RoomSearchMap;
import com.dominest.dominestbackend.domain.room.service.RoomService;
import com.dominest.dominestbackend.domain.room.roomhistory.service.RoomHistoryService;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.business.BusinessException;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import com.dominest.dominestbackend.global.util.FileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ResidentService {
    private final RoomRepository roomRepository;
    private final ResidentExcelParser residentExcelParser;
    private final ResidentRepository residentRepository;
    private final FileManager fileManager;
    private final RoomService roomService;
    private final RoomHistoryService roomHistoryService;
    private final ResidentFilePathManager residentFilePathManager;

    /** return 저장한 파일명 */
    @Transactional
    public void uploadPdf(Long id, FileManager.FilePrefix filePrefix, MultipartFile file) {
        if (fileManager.isInvalidFileExtension(file.getOriginalFilename(), FileManager.FileExt.PDF)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_EXTENSION);
        }

        Resident resident = findById(id);
        String fileNameToUpload = resident.generatePdfFileNameToStore();

        fileManager.save(filePrefix, file, fileNameToUpload);

        String prevFilename = residentFilePathManager.getPdfFilename(resident, filePrefix);
        residentFilePathManager.setPdfFilenameToResident(resident, filePrefix, fileNameToUpload);

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

            String prevFilename = residentFilePathManager.getPdfFilename(resident, filePrefix);
            residentFilePathManager.setPdfFilenameToResident(resident, filePrefix, fileNameToUpload);

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
        List<ResidentExcelParser.ResidentCreationDto> residentCreationDtos = residentExcelParser.convertToResidentExcelDto(sheet);
        return saveFromResidentCreationDtos(residentCreationDtos, residenceSemester);
    }

    private ExcelUploadResponse saveFromResidentCreationDtos(
            List<ResidentExcelParser.ResidentCreationDto> residentCreationDtos
            , ResidenceSemester residenceSemester
    ) {
        int successRow = 0;

        List<String> assignedRooms = residentCreationDtos.stream()
                .map(ResidentExcelParser.ResidentCreationDto::getAssignedRoom)
                .collect(Collectors.toUnmodifiableList());
        RoomSearchMap roomSearchMap = RoomSearchMap.from(roomRepository.findByAssignedRoomIn(assignedRooms));
        ResidentSearchMap residentSearchMap = ResidentSearchMap.from(
                residentRepository.findAllByResidenceSemester(residenceSemester)
        );

        for (ResidentExcelParser.ResidentCreationDto residentCreationDto : residentCreationDtos) {
            boolean isEmptyRow = !StringUtils.hasText(residentCreationDto.getFamilyHomeAddress());
            if (isEmptyRow) {
                continue;
            }
            Room room = roomSearchMap.getByAssignedRoom(residentCreationDto.getAssignedRoom());
            Resident resident = Resident.from(residentCreationDto, residenceSemester, room);

            // 중복검사-같은 사람이면 건너뛰기, 동명이인이면 이름 변경
            if (existsSameResidentInSemester(residentSearchMap, resident)) {
                continue;
            }
            if (residentSearchMap.existsSameNameInSemester(residenceSemester, resident.getPersonalInfo().getName())) {
                resident.changeNameWithPhoneNumber();
            }
            save(resident);
            residentSearchMap.add(resident);
            successRow++;
        }
        return ExcelUploadResponse.of(residentCreationDtos.size(), successRow);
    }

    private static boolean existsSameResidentInSemester(ResidentSearchMap residentSearchMap, Resident resident) {
        if (residentSearchMap.existsSameResidentInSemester(resident)) {
            // 엑셀 데이터상 중복이 있을 시 로그만 남기고 다음 행으로 넘어간다.
            log.warn("엑셀 데이터 저장 실패. 중복 데이터가 있어 다음으로 넘어감. 이름: {}, 학번: {}, 학기: {}" +
                            ", 방 번호: {}, 방 코드: {}", resident.getPersonalInfo().getName()
                    , resident.getStudentInfo().getStudentId(), resident.getResidenceSemester()
                    , resident.getRoom().getId(), resident.getRoom().getAssignedRoom());
            return true;
        }
        return false;
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

    private void save(Resident resident) {
        try {
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

    private void updateRoomHistory(Resident resident) {
        roomHistoryService.saveFrom(resident);
    }
}
