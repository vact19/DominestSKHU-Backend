package com.dominest.dominestbackend.domain.resident.service;

import com.dominest.dominestbackend.api.resident.response.FileBulkUploadResponse;
import com.dominest.dominestbackend.domain.common.vo.PhoneNumber;
import com.dominest.dominestbackend.domain.resident.entity.Resident;
import com.dominest.dominestbackend.domain.resident.entity.component.ResidenceSemester;
import com.dominest.dominestbackend.domain.resident.repository.ResidentRepository;
import com.dominest.dominestbackend.domain.resident.support.ResidentFilePathManager;
import com.dominest.dominestbackend.domain.room.entity.Room;
import com.dominest.dominestbackend.domain.room.repository.RoomRepository;
import com.dominest.dominestbackend.global.exception.exceptions.business.BusinessException;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import com.dominest.dominestbackend.global.util.FileManager;
import com.dominest.dominestbackend.global.util.UuidHolder;
import com.dominest.dominestbackend.global.util.mock.FixedUuidHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
class ResidentServiceTest {

    @Autowired ResidentService residentService;
    @Autowired ResidentRepository residentRepository;
    @Autowired ResidentFilePathManager residentFilePathManager;
    @Autowired RoomRepository roomRepository;
    @SpyBean FileManager fileManager;

    private static final String fixedUuid = UUID.randomUUID().toString();
    @TestConfiguration
    static class TestConfig {
        // 테스트용 UuidHolder 고정값 설정
        @Bean
        public UuidHolder uuidHolder() {
            return new FixedUuidHolder(fixedUuid);
        }
    }

    @AfterEach
    void tearDown() {
        residentRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();
    }

    /**
     * File upload, delete 는 Mocking 하고,
     * DB 는 실제 객체 사용함.
     */
    @DisplayName("지정한 ID를 가진 입사생을 대상으로 PDF서류를 등록할 수 있다")
    @Test
    void uploadDocument_when_validRequest_should_uploadNewFile() {
        //given
        Resident savedResident = saveDummyResident("김철수", "B1017A", "010-0100-0100");
        FileManager.FilePrefix filePrefix = FileManager.FilePrefix.RESIDENT_ADMISSION;
        MultipartFile mockMultipartFile = new MockMultipartFile("filename", "originalFileName.pdf", MediaType.APPLICATION_PDF_VALUE, "test".getBytes());

        doNothing().when(fileManager).save(any(), any(MultipartFile.class), any());
        doNothing().when(fileManager).deleteFile(any(), any(String.class));

        //when
        residentService.uploadDocument(savedResident.getId(), filePrefix, mockMultipartFile);

        //then
        String expectedFileName = savedResident.getPersonalInfo().getName() + "-" + fixedUuid + "." + FileManager.FileExt.PDF.label;
        Resident updatedResident = residentService.getById(savedResident.getId());
        String savedFileName = residentFilePathManager.getFilename(updatedResident, filePrefix);

        assertThat(savedFileName).isEqualTo(expectedFileName);
    }

    @DisplayName("파일 단건 업로드 시 확장자가 PDF가 아닐 경우 예외가 발생한다")
    @Test
    void uploadDocument_when_invalidExt_should_throwEx() {
        //given
        String fileNameWithInvalidExt = "originalFileName.txt";
        MultipartFile mockMultipartFile = new MockMultipartFile("filename", fileNameWithInvalidExt, MediaType.APPLICATION_PDF_VALUE, "test".getBytes());
        FileManager.FilePrefix filePrefix = FileManager.FilePrefix.RESIDENT_ADMISSION;

        //when
        assertThatThrownBy(() -> residentService.uploadDocument(1L, filePrefix, mockMultipartFile))
                //then
                .isInstanceOf(BusinessException.class)
                .hasMessage("파일 확장자가 유효하지 않습니다.");
    }

    @DisplayName("등록되지 않은 입사생 ID를 사용하여 서류를 단건 업로드할 경우 예외가 발생한다")
    @Test
    void uploadDocument_when_unregisteredResidentId_should_throwEx() {
        //given
        MultipartFile mockMultipartFile = new MockMultipartFile("filename", "originalFileName.pdf", MediaType.APPLICATION_PDF_VALUE, "test".getBytes());
        FileManager.FilePrefix filePrefix = FileManager.FilePrefix.RESIDENT_ADMISSION;

        //when
        assertThatThrownBy(() -> residentService.uploadDocument(1L, filePrefix, mockMultipartFile))
                //then
                .isInstanceOf(ResourceNotFoundException.class)
        ;
    }

    @DisplayName("정해진 학기에 파일명과 이름이 일치하는 입사생을 대상으로 PDF파일을 등록할 수 있다")
    @Test
    void uploadDocuments_when_validRequest_should_uploadNewFiles() {
        //given
        Resident savedResident1 = saveDummyResident("파일명과 이름이 일치할 입사생", "B1017A", "010-0100-0100");
        saveDummyResident("파일명과 이름이 일치하지 않을 입사생", "B1018A", "010-0100-0101");

        List<MultipartFile> mockMultipartFiles = List.of(
                new MockMultipartFile("filename", "파일명과 이름이 일치할 입사생.pdf", MediaType.APPLICATION_PDF_VALUE, "test".getBytes()),
                new MockMultipartFile("filename", "wrongFileName.pdf", MediaType.APPLICATION_PDF_VALUE, "test".getBytes())
        );
        FileManager.FilePrefix filePrefix = FileManager.FilePrefix.RESIDENT_ADMISSION;

        doNothing().when(fileManager).save(any(), any(MultipartFile.class), any());
        doNothing().when(fileManager).deleteFile(any(), any(String.class));

        //when
        FileBulkUploadResponse fileBulkUploadResponse = residentService.uploadDocuments(filePrefix, mockMultipartFiles, ResidenceSemester.S2024_1);

        //then
        String expectedFileName = savedResident1.getPersonalInfo().getName() + "-" + fixedUuid + "." + FileManager.FileExt.PDF.label;
        Resident updatedResident = residentService.getById(savedResident1.getId());
        String savedFileName = residentFilePathManager.getFilename(updatedResident, filePrefix);

        // 실제로 DB상에 저장된 파일명 검증
        assertThat(savedFileName).isEqualTo(expectedFileName);
        // 응답 DTO 검증
        assertThat(fileBulkUploadResponse.getSuccessCount()).isEqualTo(1);
        assertThat(fileBulkUploadResponse.getFiles())
                .hasSize(2)
                .extracting("filename", "status", "failReason")
                .containsExactly(
                        tuple("파일명과 이름이 일치할 입사생.pdf", "OK", null),
                        tuple("wrongFileName.pdf", "FAILED", "학생명이 파일명과 일치하지 않습니다.")
                );
    }

    @DisplayName("복수의 서류 업로드 시 한 건도 업로드하지 못하면 예외가 발생한다")
    @Test
    void uploadDocuments_should_throwEx_when_noFileIsUploaded() {
        //given
        List<MultipartFile> mockMultipartFiles = List.of();
        FileManager.FilePrefix filePrefix = FileManager.FilePrefix.RESIDENT_ADMISSION;

        //when
        assertThatThrownBy(() -> residentService.uploadDocuments(filePrefix, mockMultipartFiles, ResidenceSemester.S2024_1))
                //then
                .isInstanceOf(BusinessException.class)
                .hasMessage("파일명과 현재 입사자 이름이 일치하지 않아 파일이 업로드되지 않았습니다.");
    }

    /**
     * Resident 를 찾을 수 없는 경우에도 반복문을 건너뛰게 되므로 dummyResident 를 호출하지 않았음.
     * 실제 파일 I/O가 일어나기 전에 Continue 되므로 FileManager 를 Mocking 하지 않았음.
     */
    @DisplayName("복수의 서류 업로드 시 파일 혹은 파일이름이 비어있을 경우 해당 파일의 업로드를 건너뛴다")
    @Test
    void uploadDocuments_should_skip_when_fileIsEmpty() {
        //given
        List<MultipartFile> mockMultipartFiles = List.of(
                new MockMultipartFile("filename", "", MediaType.APPLICATION_PDF_VALUE, "test".getBytes()),
                new MockMultipartFile("filename", "emptyFile", MediaType.APPLICATION_PDF_VALUE, new byte[]{})
        );
        FileManager.FilePrefix filePrefix = FileManager.FilePrefix.RESIDENT_ADMISSION;

        //when
        assertThatThrownBy(() -> residentService.uploadDocuments(filePrefix, mockMultipartFiles, ResidenceSemester.S2024_1))
                //then
                .isInstanceOf(BusinessException.class)
                .hasMessage("파일명과 현재 입사자 이름이 일치하지 않아 파일이 업로드되지 않았습니다.");
    }

    private Resident saveDummyResident(String name, String assignedRoom, String phoneNumber) {
        Resident.PersonalInfo personalInfo = new Resident.PersonalInfo(
                name,
                "M",
                new PhoneNumber(phoneNumber),
                LocalDate.of(2000, 1, 15)
        );

        Resident.StudentInfo studentInfo = new Resident.StudentInfo(
                "2023001234",
                "컴퓨터공학과",
                "3학년"
        );

        Resident.ResidenceDateInfo dateInfo = new Resident.ResidenceDateInfo(
                LocalDate.of(2023, 3, 2),
                null,
                LocalDate.of(2023, 3, 1),
                LocalDate.of(2023, 12, 21)
        );

        Resident.ResidenceInfo residenceInfo = new Resident.ResidenceInfo(
                "2023SMSK02",
                "입주중",
                "AY",
                "S001",
                "1동",
                "12345",
                "서울시 강남구 테헤란로 123"
        );

        ResidenceSemester residenceSemester = ResidenceSemester.S2024_1;

        Room room = new Room(assignedRoom, Integer.parseInt(assignedRoom.substring(1, 3)), Room.Dormitory.HAENGBOK);
        roomRepository.save(room);

        Resident resident = new Resident(personalInfo, studentInfo, dateInfo, residenceInfo, residenceSemester, room);
        return residentRepository.save(resident);
    }
}
