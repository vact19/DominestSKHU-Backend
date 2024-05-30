package com.dominest.dominestbackend.api.resident.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExcelUploadResponse {
    int originalRow;
    int successRow;

    public String getResultMsg() {
        return originalRow + "개의 행 중 " + successRow + "개의 행이 성공적으로 업로드되었습니다.";
    }
    public static ExcelUploadResponse of(int originalRow, int successRow) {
        return new ExcelUploadResponse(originalRow, successRow);
    }
}
