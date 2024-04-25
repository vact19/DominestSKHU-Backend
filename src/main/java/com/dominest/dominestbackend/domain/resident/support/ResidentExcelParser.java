package com.dominest.dominestbackend.domain.resident.support;

import com.dominest.dominestbackend.domain.post.inspection.floor.room.entity.InspectionRoom;
import com.dominest.dominestbackend.domain.post.inspection.floor.room.component.InspectionResidentInfo;
import com.dominest.dominestbackend.domain.room.entity.Room;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.business.BusinessException;
import com.dominest.dominestbackend.global.exception.exceptions.external.ExternalServiceException;
import com.dominest.dominestbackend.global.exception.exceptions.external.file.FileIOException;
import com.dominest.dominestbackend.global.util.ExcelParser;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ResidentExcelParser {
    private final ExcelParser excelParser;

    /**
     * 엑셀 데이터를 파싱하기 위한 상수.
     * 6번 인덱스는 '기숙사' 컬럼으로, 원본 엑셀데이터에서 'B'로 고정되어있으므로 받지 않음.
     * 10번 인덱스는 '호실' 컬럼으로, 원본 엑셀데이터에서 '2'로 고정되어있으므로 받지 않음.
     */
    public static final int NAME_COLUMN_INDEX = 0; public static final int GENDER_COLUMN_INDEX = 1; public static final int STUDENT_ID_COLUMN_INDEX = 2; public static final int SEMESTER_COLUMN_INDEX = 3; public static final int CURRENT_STATUS_COLUMN_INDEX = 4; public static final int DATE_OF_BIRTH_COLUMN_INDEX = 5;
    public static final int MAJOR_COLUMN_INDEX = 7; public static final int GRADE_COLUMN_INDEX = 8; public static final int PERIOD_COLUMN_INDEX = 9; public static final int ASSIGNED_ROOM_COLUMN_INDEX = 11; public static final int ADMISSION_DATE_COLUMN_INDEX = 12; public static final int LEAVING_DATE_COLUMN_INDEX = 13;
    public static final int SEMESTER_START_DATE_COLUMN_INDEX = 14; public static final int SEMESTER_END_DATE_COLUMN_INDEX = 15; public static final int PHONE_NUMBER_COLUMN_INDEX = 16; public static final int SOCIAL_CODE_COLUMN_INDEX = 17; public static final int SOCIAL_NAME_COLUMN_INDEX = 18; public static final int FAMILY_HOME_ZIP_CODE_COLUMN_INDEX = 19; public static final int FAMILY_HOME_ADDRESS_COLUMN_INDEX = 20;

    public static final int RESIDENT_COLUMN_COUNT = 21;

    public List<ResidentCreationDto> convertToResidentExcelDto(List<List<String>> sheet) {
        validateResidentColumnCount(sheet);
        // 첫 3줄 제거 후 유효 데이터만 추출
        sheet.remove(0); sheet.remove(0);sheet.remove(0);

        return sheet.stream()
                .map(ResidentCreationDto::from)
                .collect(Collectors.toUnmodifiableList());
    }

    private void validateResidentColumnCount(List<List<String>> sheet) {
        Integer sheetColumnCount = Optional.ofNullable(sheet.get(0))
                .map(List::size)
                .orElse(0);

        if (sheetColumnCount != RESIDENT_COLUMN_COUNT){
            throw new BusinessException("읽어들인 컬럼 개수가 " +
                    RESIDENT_COLUMN_COUNT + "개가 아닙니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 통과차수별 방 정보와 소속된 사생의 정보를 반환.
    public void createAndRespondResidentInfoWithInspectionRoom(String filename, String sheetName, HttpServletResponse response, List<InspectionRoom> inspectionRooms) {
        if (excelParser.isNotExcelExt(filename)) {
            throw new ExternalServiceException(ErrorCode.INVALID_FILE_EXTENSION);
        }

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        // try-with-resources를 사용하여 워크북 생성
        try (Workbook workbook = new XSSFWorkbook()) {
            // 새로운 워크시트 생성
            Sheet sheet = workbook.createSheet(sheetName);
            // 헤더 행 작성
            Row headerRow = sheet.createRow(0);
            String[] headers = {"호실", "이름", "전화번호", "학번", "벌점", "통과차수"};

            for (int i=0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 보기 편하게끔, 전화번호, 학번 컬럼은 width를 따로 설정
            int columnWidth14 = 14 * 256; // 14문자 너비
            int columnWidth10 = 10 * 256; // 10문자 너비
            sheet.setColumnWidth(2, columnWidth14);
            sheet.setColumnWidth(3, columnWidth10);

            // 데이터 작성
            for (int rowNum = 1; rowNum <= inspectionRooms.size(); rowNum++) {
                Row row = sheet.createRow(rowNum);

                InspectionRoom inspectionRoom = inspectionRooms.get(rowNum - 1);
                InspectionResidentInfo inspectionResidentInfo = inspectionRoom.getInspectionResidentInfo();
                Room room = inspectionRoom.getRoom();
                String assignedRoom = room != null ? room.getAssignedRoom() : "";

                row.createCell(0).setCellValue(assignedRoom);
                row.createCell(1).setCellValue(inspectionResidentInfo == null ? "" : inspectionResidentInfo.getName());
                row.createCell(2).setCellValue(inspectionResidentInfo == null ? "" : inspectionResidentInfo.getPhoneNo());
                row.createCell(3).setCellValue(inspectionResidentInfo == null ? "" : inspectionResidentInfo.getStudentId());
                row.createCell(4).setCellValue(inspectionRoom.getPassState().getPenalty());
                row.createCell(5).setCellValue(inspectionRoom.getPassState().getLabel());
            }

            // 파일 내보내기
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new FileIOException(ErrorCode.FILE_CANNOT_BE_SENT, e);
        }
    }

    // 점검표 화면의 내용 전체를 다운로드
    public void createAndRespondAllDataWithInspectionRoom(String filename, String sheetName, HttpServletResponse response, List<InspectionRoom> inspectionRooms) {
        if (excelParser.isNotExcelExt(filename)) {
            throw new ExternalServiceException(ErrorCode.INVALID_FILE_EXTENSION);
        }

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(filename, StandardCharsets.UTF_8)
                .build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        // try-with-resources를 사용하여 워크북 생성
        try (Workbook workbook = new XSSFWorkbook()) {
            // 새로운 워크시트 생성
            Sheet sheet = workbook.createSheet(sheetName);

            /* 첫 행에 부가정보 표시 START*/
            Row firstRow = sheet.createRow(0);

            // 셀 생성 및 데이터 입력
            Cell mergedDataCell = firstRow.createCell(0);
            mergedDataCell.setCellValue("입사생 정보가 비어있을 경우 빈 방입니다.");

            // 1행의 1열부터 5열까지 셀을 병합
            sheet.addMergedRegion(new CellRangeAddress(
                    0, // first row (0-based)
                    0, // last row
                    0, // first column (0-based)
                    4  // last column
            ));
            /* 첫 행에 부가정보 표시 END*/

            // 헤더 행 작성
            Row headerRow = sheet.createRow(1);
            String[] headers = {"호실", "이름", "전화번호", "학번", "벌점", "통과차수", "실내", "쓰레기방치", "화장실", "샤워실", "보관금지", "기타"};

            for (int i=0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 보기 편하게끔, 전화번호, 학번 컬럼은 width를 따로 설정
            int columnWidth14 = 14 * 256; // 14문자 너비
            int columnWidth10 = 10 * 256; // 10문자 너비
            sheet.setColumnWidth(2, columnWidth14);
            sheet.setColumnWidth(3, columnWidth10);

            // 데이터 작성
            int dataStartRow = 2;
            for (int rowNum = dataStartRow; rowNum <= inspectionRooms.size() + dataStartRow - 1; rowNum++) {
                Row row = sheet.createRow(rowNum);

                InspectionRoom inspectionRoom = inspectionRooms.get(rowNum - dataStartRow);
                InspectionResidentInfo inspectionResidentInfo = inspectionRoom.getInspectionResidentInfo();
                Room room = inspectionRoom.getRoom();
                String assignedRoom = room != null ? room.getAssignedRoom() : "";

                // 실내 쓰레기방치 화장실 샤워실 보관금지
                //indoor leavedTrash toilet shower prohibitedItem
                row.createCell(0).setCellValue(assignedRoom);
                row.createCell(1).setCellValue(inspectionResidentInfo == null ? "" : inspectionResidentInfo.getName());
                row.createCell(2).setCellValue(inspectionResidentInfo == null ? "" : inspectionResidentInfo.getPhoneNo());
                row.createCell(3).setCellValue(inspectionResidentInfo == null ? "" : inspectionResidentInfo.getStudentId());
                row.createCell(4).setCellValue(inspectionRoom.getPassState().getPenalty());
                row.createCell(5).setCellValue(inspectionRoom.getPassState().getLabel());
                row.createCell(6).setCellValue(inspectionRoom.isIndoor() ? "O" : "X");
                row.createCell(7).setCellValue(inspectionRoom.isLeavedTrash() ? "O" : "X");
                row.createCell(8).setCellValue(inspectionRoom.isToilet() ? "O" : "X");
                row.createCell(9).setCellValue(inspectionRoom.isShower() ? "O" : "X");
                row.createCell(10).setCellValue(inspectionRoom.isProhibitedItem() ? "O" : "X");
                row.createCell(11).setCellValue(inspectionRoom.getEtc() == null ? "" : inspectionRoom.getEtc());
            }

            // 파일 내보내기
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new FileIOException(ErrorCode.FILE_CANNOT_BE_SENT, e);
        }
    }

    @Getter
    @Builder
    public static class ResidentCreationDto {
        private final String name;
        private final String gender;
        private final String studentId;
        private final String semester;
        private final String currentStatus;
        private final String dateOfBirth;

        private final String major;
        private final String grade;
        private final String period;
        private final String assignedRoom;
        private final String admissionDate;
        private final String leavingDate;
        private final String semesterStartDate;
        private final String semesterEndDate;

        private final String phoneNumber;
        private final String socialCode;
        private final String socialName;
        private final String familyHomeZipCode;
        private final String familyHomeAddress;

        private static ResidentCreationDto from(List<String> data) {
            return ResidentCreationDto.builder()
                    .name(data.get(NAME_COLUMN_INDEX))
                    .gender(data.get(GENDER_COLUMN_INDEX))
                    .studentId(data.get(STUDENT_ID_COLUMN_INDEX))
                    .semester(data.get(SEMESTER_COLUMN_INDEX))
                    .currentStatus(data.get(CURRENT_STATUS_COLUMN_INDEX))
                    .dateOfBirth(data.get(DATE_OF_BIRTH_COLUMN_INDEX))
                    .major(data.get(MAJOR_COLUMN_INDEX))
                    .grade(data.get(GRADE_COLUMN_INDEX))
                    .period(data.get(PERIOD_COLUMN_INDEX))
                    .assignedRoom(data.get(ASSIGNED_ROOM_COLUMN_INDEX))
                    .admissionDate(data.get(ADMISSION_DATE_COLUMN_INDEX))
                    .leavingDate(data.get(LEAVING_DATE_COLUMN_INDEX))
                    .semesterStartDate(data.get(SEMESTER_START_DATE_COLUMN_INDEX))
                    .semesterEndDate(data.get(SEMESTER_END_DATE_COLUMN_INDEX))
                    .phoneNumber(data.get(PHONE_NUMBER_COLUMN_INDEX))
                    .socialCode(data.get(SOCIAL_CODE_COLUMN_INDEX))
                    .socialName(data.get(SOCIAL_NAME_COLUMN_INDEX))
                    .familyHomeZipCode(data.get(FAMILY_HOME_ZIP_CODE_COLUMN_INDEX))
                    .familyHomeAddress(data.get(FAMILY_HOME_ADDRESS_COLUMN_INDEX))
                    .build();
        }
    }
}
