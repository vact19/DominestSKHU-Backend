package com.dominest.dominestbackend.api.resident.request;

import com.dominest.dominestbackend.domain.resident.component.ResidenceSemester;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;


@Getter
@NoArgsConstructor
public class ExcelUploadRequest {
    @NotNull(message = "엑셀파일을 첨부해주세요.")
    MultipartFile file;
    @NotNull(message = "거주학기를 입력해주세요.")
    ResidenceSemester residenceSemester;
}



