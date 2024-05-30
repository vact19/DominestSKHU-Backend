package com.dominest.dominestbackend.global.util;

import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.external.ExternalServiceException;
import com.dominest.dominestbackend.global.exception.exceptions.external.file.FileIOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 엑셀 파일 관련 작업을 하는 유틸리티 클래스
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class ExcelParser {

    public List<List<String>> parse(MultipartFile file) {
        if (isNotExcelFile(file)) {
            throw new ExternalServiceException(ErrorCode.INVALID_FILE_EXTENSION);
        }

        List<List<String>> data = new ArrayList<>();
        Sheet sheet;

        try (
                InputStream inputStream = file.getInputStream();
                Workbook workbook = WorkbookFactory.create(inputStream)
        ) {
            sheet = workbook.getSheetAt(0);

            // sheet extend Iterable<Row>.
            for (Row row : sheet) {
                Iterator<Cell> cellIterator = row.cellIterator();

                List<String> rowData = new ArrayList<>(); // default capacity 10이므로 컬럼개수만큼 공간 확보
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String cellValue;
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            cellValue = NumberToTextConverter.toText(cell.getNumericCellValue());
                            break;
                        case BLANK:
                            cellValue = "";
                            break;
                        default:
                            cellValue = cell.getStringCellValue();
                            break;
                    }
                    rowData.add(cellValue);
                }
                data.add(rowData);
            }
            return data;
        } catch (IOException e) {
            throw new FileIOException(ErrorCode.MULTIPART_FILE_CANNOT_BE_READ, e);
        }
    }

    private boolean isNotExcelFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return isNotExcelExt(filename);
    }

    public boolean isNotExcelExt(String fileName) {
        String ext = extractExt(fileName);
        return !("xlsx".equals(ext) || "xls".equals(ext));
    }

    private String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos +1);
    }
}
