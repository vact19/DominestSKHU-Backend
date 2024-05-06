package com.dominest.dominestbackend.global.converter;

import com.dominest.dominestbackend.domain.resident.support.ResidentDocumentType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class ResidentDocumentTypeConverter implements Converter<String, ResidentDocumentType> {
    @Override
    public ResidentDocumentType convert(String source) {
        return ResidentDocumentType.from(source);
    }
}
