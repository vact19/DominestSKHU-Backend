package com.dominest.dominestbackend.api.resident.controller;


import com.dominest.dominestbackend.api.common.ResponseTemplate;
import com.dominest.dominestbackend.api.resident.request.ExcelUploadRequest;
import com.dominest.dominestbackend.api.resident.request.SaveResidentRequest;
import com.dominest.dominestbackend.api.resident.response.ExcelUploadResponse;
import com.dominest.dominestbackend.api.resident.response.FileBulkUploadResponse;
import com.dominest.dominestbackend.api.resident.response.ResidentListResponse;
import com.dominest.dominestbackend.api.resident.response.ResidentDocumentListResponse;
import com.dominest.dominestbackend.domain.resident.support.ResidentDocumentType;
import com.dominest.dominestbackend.domain.resident.entity.Resident;
import com.dominest.dominestbackend.domain.resident.service.ResidentService;
import com.dominest.dominestbackend.domain.resident.entity.component.ResidenceSemester;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.external.file.FileIOException;
import com.dominest.dominestbackend.global.util.ExcelParser;
import com.dominest.dominestbackend.global.util.FileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ResidentController {

    private final ResidentService residentService;
    private final FileManager fileManager;
    private final ExcelParser excelParser;

    // 엑셀로 업로드
    @PostMapping("/residents/upload-excel")
    public ResponseEntity<ResponseTemplate<ExcelUploadResponse>> handleFileUpload(
            @ModelAttribute @Valid ExcelUploadRequest request
    ){
        // 엑셀 파싱
        List<List<String>> sheet= excelParser.parse(request.getFile());

        ExcelUploadResponse response = residentService.excelUpload(sheet, request.getResidenceSemester());
        String resultMsg = response.getResultMsg();

        if (response.getSuccessRow() <= 0) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            new ResponseTemplate<>(HttpStatus.OK, resultMsg));
        } else {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            new ResponseTemplate<>(HttpStatus.CREATED, resultMsg)
                    );
        }
    }

    // 전체조회
    @GetMapping("/residents")
    public ResponseTemplate<ResidentListResponse> handleGetAllResident(
            @RequestParam(required = true) ResidenceSemester residenceSemester
    ){
        List<Resident> residents = residentService.getAllResidentByResidenceSemester(residenceSemester);

        ResidentListResponse response = ResidentListResponse.from(residents);
        return new ResponseTemplate<>(HttpStatus.OK, "입사생 목록 조회 성공", response);
    }

    // (테스트용) 입사생 데이터 전체삭제
    @DeleteMapping("/residents")
    public ResponseEntity<ResponseTemplate<Void>> handleDeleteAllResident(){
        residentService.deleteAllResident();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 입사생 단건 등록. 단순 DTO 변환 후 저장만 하면 될듯
    @PostMapping("/residents")
    public ResponseEntity<ResponseTemplate<Void>> handleSaveResident(
            @RequestBody @Valid SaveResidentRequest request
    ){
        residentService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 입사생 수정
    @PatchMapping("/residents/{id}")
    public ResponseEntity<ResponseTemplate<Void>> handleUpdateResident(
            @PathVariable Long id, @RequestBody @Valid SaveResidentRequest request
    ){
        residentService.updateResident(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 입사생 삭제
    @DeleteMapping("/residents/{id}")
    public ResponseEntity<ResponseTemplate<Void>> handleDeleteResident(@PathVariable Long id){
        residentService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 특정 입사생의 서류 조회
    @GetMapping("/residents/{id}/documents")
    public void handleGetDocument(
            @PathVariable Long id
            , @RequestParam(required = true) ResidentDocumentType residentDocumentType
            , HttpServletResponse response
    ){
        Resident resident = residentService.findById(id);

        String filename = residentDocumentType.getDocumentFileName(resident);
        FileManager.FilePrefix filePrefix = residentDocumentType.toFilePrefix();

        byte[] bytes = fileManager.getByteArr(filePrefix, filename);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);

        try(ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new FileIOException(ErrorCode.FILE_CANNOT_BE_SENT, e);
        }
    }

    // 입사생 서류 단건 업로드
    @PostMapping("/residents/{id}/documents")
    public ResponseEntity<ResponseTemplate<String>> handleDocumentUpload(
            @PathVariable Long id
            , @RequestParam(required = true) MultipartFile documentFile
            , @RequestParam(required = true) ResidentDocumentType residentDocumentType
    ){
        FileManager.FilePrefix filePrefix = residentDocumentType.toFilePrefix();

        residentService.uploadDocument(id, filePrefix, documentFile);
        ResponseTemplate<String> responseTemplate = new ResponseTemplate<>(
                HttpStatus.CREATED, "입사생 서류 업로드 완료");
        return ResponseEntity
                .created(URI.create("/residents/"+id+"/documents"))
                .body(responseTemplate);
    }

    // 특정학기 입사생 서류 전체 업로드
    @PostMapping("/residents/documents")
    public ResponseEntity<ResponseTemplate<FileBulkUploadResponse>> handleDocumentUpload(
            @RequestParam(required = true) List<MultipartFile> documentFiles
            , @RequestParam(required = true) ResidenceSemester residenceSemester
            , @RequestParam(required = true) ResidentDocumentType residentDocumentType
    ){
        FileManager.FilePrefix filePrefix = residentDocumentType.toFilePrefix();
        FileBulkUploadResponse response = residentService.uploadDocuments(filePrefix, documentFiles, residenceSemester);

        ResponseTemplate<FileBulkUploadResponse> responseTemplate = new ResponseTemplate<>(HttpStatus.CREATED,
                "입사생 서류 업로드 완료. 저장된 파일 수: " + response.getSuccessCount() + "개", response);
        return ResponseEntity
                .created(URI.create("/residents/documents"))
                .body(responseTemplate);
    }

    // 해당차수 입사생 전체 서류 조회
    @GetMapping("/residents/documents")
    public ResponseTemplate<ResidentDocumentListResponse> handleGetAllDocuments(
            @RequestParam(required = true) ResidenceSemester residenceSemester
    ){
        List<Resident> residents = residentService.findAllByResidenceSemester(residenceSemester);

        ResidentDocumentListResponse response = ResidentDocumentListResponse.from(residents);
        return new ResponseTemplate<>(HttpStatus.OK
                , "입사생 서류 url 조회 성공"
                , response);
    }
}
