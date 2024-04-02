package com.dominest.dominestbackend.global.exception.exceptions.external.file;


import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.external.ExternalServiceException;

public class FileIOException extends ExternalServiceException {
    public FileIOException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public FileIOException(ErrorCode errorCode) {
        super(errorCode);
    }
}
