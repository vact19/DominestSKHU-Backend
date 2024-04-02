package com.dominest.dominestbackend.global.exception.exceptions.external.db;

import com.dominest.dominestbackend.domain.common.Datasource;

import com.dominest.dominestbackend.global.exception.exceptions.external.ExternalServiceException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ExternalServiceException {
    private static final HttpStatus NOT_FOUND = HttpStatus.NOT_FOUND;

    public ResourceNotFoundException(Datasource datasource, long id) {
        super("Id=[" + id + "]인 " + datasource.name() + "을 찾을 수 없습니다."
                , NOT_FOUND);
    }

    public ResourceNotFoundException(Datasource datasource, String attribute, String value) {
        super(attribute + "=[" + value + "]인 " + datasource.name() + "을 찾을 수 없습니다."
                , NOT_FOUND);
    }
}
