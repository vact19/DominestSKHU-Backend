package com.dominest.dominestbackend.api.resident.response;

import lombok.*;


import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class FileBulkUploadResponse {
    List<UploadDto> files = new ArrayList<>();
    int successCount = 0;

    public void addSuccessCount(){
        successCount++;
    }

    public void addToDtoList(String filename, String status, String failReason){
        UploadDto uploadDto = UploadDto.builder()
                .filename(filename)
                .status(status)
                .failReason(failReason)
                .build();
        files.add(uploadDto);
    }

    @Getter
    @Builder
    private static class UploadDto{
        String filename;
        String status;
        String failReason;
    }
}
