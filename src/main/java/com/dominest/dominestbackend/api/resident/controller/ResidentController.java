package com.dominest.dominestbackend.api.resident.controller;


import com.dominest.dominestbackend.api.common.ResponseTemplate;
import com.dominest.dominestbackend.api.resident.request.ExcelUploadRequest;
import com.dominest.dominestbackend.api.resident.request.SaveResidentRequest;
import com.dominest.dominestbackend.api.resident.response.ExcelUploadResponse;
import com.dominest.dominestbackend.api.resident.response.PdfBulkUploadResponse;
import com.dominest.dominestbackend.api.resident.response.ResidentListResponse;
import com.dominest.dominestbackend.api.resident.response.ResidentPdfListResponse;
import com.dominest.dominestbackend.api.resident.util.PdfType;
import com.dominest.dominestbackend.domain.resident.Resident;
import com.dominest.dominestbackend.domain.resident.ResidentService;
import com.dominest.dominestbackend.domain.resident.component.ResidenceSemester;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.file.FileIOException;
import com.dominest.dominestbackend.global.util.ExcelUtil;
import com.dominest.dominestbackend.global.util.FileService;
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
    private final FileService fileService;

    // 엑셀로 업로드
    @PostMapping("/residents/upload-excel")
    public ResponseEntity<ResponseTemplate<ExcelUploadResponse>> handleFileUpload(
            @ModelAttribute @Valid ExcelUploadRequest request
    ){
        // 엑셀 파싱
        List<List<String>> sheet= ExcelUtil.parseExcel(request.getFile());
        ExcelUtil.checkResidentColumnCount(sheet);

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
        List<Resident> residents = residentService.getAllResidentByResidenceSemesterFetchRoom(residenceSemester);

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
    public ResponseEntity<ResponseTemplate<Void>> handleSaveResident(@RequestBody @Valid SaveResidentRequest request){
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

    // 특정 입사생의 PDF 조회
    @GetMapping("/residents/{id}/pdf")
    public void handleGetPdf(@PathVariable Long id, @RequestParam(required = true) PdfType pdfType,
                                       HttpServletResponse response){
        // filename 가져오기.
        Resident resident = residentService.findById(id);

        // PdfType에 따라 입사 혹은 퇴사신청서 filename 가져오기
        String filename = pdfType.getPdfFileName(resident);
        FileService.FilePrefix filePrefix = pdfType.toFilePrefix();

        // PDF 파일 읽기
        byte[] bytes = fileService.getByteArr(filePrefix, filename);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);

        try(ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new FileIOException(ErrorCode.FILE_CANNOT_BE_SENT, e);
        }
    }

    // PDF 단건 업로드
    @PostMapping("/residents/{id}/pdf")
    public ResponseEntity<ResponseTemplate<String>> handlePdfUpload(@PathVariable Long id, @RequestParam(required = true) MultipartFile pdf,
                                                                    @RequestParam(required = true) PdfType pdfType){
        FileService.FilePrefix filePrefix = pdfType.toFilePrefix();

        residentService.uploadPdf(id, filePrefix, pdf);
        ResponseTemplate<String> responseTemplate = new ResponseTemplate<>(HttpStatus.CREATED, "pdf 업로드 완료");
        return ResponseEntity
                .created(URI.create("/residents/"+id+"/pdf"))
                .body(responseTemplate);
    }

    // PDF 전체 업로드
    @PostMapping("/residents/pdf")
    public ResponseEntity<ResponseTemplate<PdfBulkUploadResponse>> handlePdfUpload(
            @RequestParam(required = true) List<MultipartFile> pdfs
            , @RequestParam(required = true) ResidenceSemester residenceSemester
            , @RequestParam(required = true) PdfType pdfType
    ){
        FileService.FilePrefix filePrefix = pdfType.toFilePrefix();
        PdfBulkUploadResponse response = residentService.uploadPdfs(filePrefix, pdfs, residenceSemester);

        ResponseTemplate<PdfBulkUploadResponse> responseTemplate = new ResponseTemplate<>(HttpStatus.CREATED,
                "pdf 업로드 완료. 저장된 파일 수: " + response.getSuccessCount() + "개", response);
        return ResponseEntity
                .created(URI.create("/residents/pdf"))
                .body(responseTemplate);
    }

    // 해당차수 입사생 전체 PDF 조회
    @GetMapping("/residents/pdf")
    public ResponseTemplate<ResidentPdfListResponse> handleGetAllPdfs(@RequestParam(required = true) ResidenceSemester residenceSemester){

        List<Resident> residents = residentService.findAllByResidenceSemester(residenceSemester);

        ResidentPdfListResponse response = ResidentPdfListResponse.from(residents);
        return new ResponseTemplate<>(HttpStatus.OK
                , "pdf url 조회 성공"
                , response);
    }
}