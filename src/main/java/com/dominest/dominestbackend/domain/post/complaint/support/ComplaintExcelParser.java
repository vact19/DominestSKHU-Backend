package com.dominest.dominestbackend.domain.post.complaint.support;

import com.dominest.dominestbackend.domain.post.complaint.entity.Complaint;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.external.ExternalServiceException;
import com.dominest.dominestbackend.global.exception.exceptions.external.file.FileIOException;
import com.dominest.dominestbackend.global.util.ExcelParser;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ComplaintExcelParser {
    private final ExcelParser excelParser;

    // 민원처리내역의 모든 데이터를 엑셀로 반환한다.
    public void createAndRespondAllDataWithComplaint(String filename, String sheetName, HttpServletResponse response, List<Complaint> complaints) {
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
            // 헤더 행 작성
            Row headerRow = sheet.createRow(0);
            String[] headers = {"민원번호", "방번호", "민원결과", "민원내역", "민원답변",  "민원인", "민원접수일"};

            for (int i=0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 보기 편하게끔 민원내역 민원답변, 민원접수일 컬럼은 width를 따로 설정
            int columnWidth30 = 30 * 256; // 23문자 너비
            sheet.setColumnWidth(3, columnWidth30);
            sheet.setColumnWidth(4, columnWidth30);
            int columnWidth12 = 12 * 256; // 14문자 너비
            sheet.setColumnWidth(6, columnWidth12);

            // 데이터 작성
            for (int rowNum = 1; rowNum <= complaints.size(); rowNum++) {
                Row row = sheet.createRow(rowNum);

                Complaint complaint = complaints.get(rowNum - 1);

                // "민원번호", "방번호", "처리결과", "민원내역", "민원답변",  "민원인", "민원접수일"
                row.createCell(0).setCellValue(complaint.getId());
                row.createCell(1).setCellValue(complaint.getRoomNo());
                row.createCell(2).setCellValue(complaint.getProcessState().state);
                row.createCell(3).setCellValue(complaint.getComplaintCause());
                row.createCell(4).setCellValue(complaint.getComplaintResolution());
                row.createCell(5).setCellValue(complaint.getName());
                row.createCell(6).setCellValue(complaint.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }

            // 파일 내보내기
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new FileIOException(ErrorCode.FILE_CANNOT_BE_SENT, e);
        }
    }
}
