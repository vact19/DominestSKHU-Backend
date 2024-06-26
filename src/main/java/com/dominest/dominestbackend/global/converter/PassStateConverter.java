package com.dominest.dominestbackend.global.converter;

import com.dominest.dominestbackend.domain.post.inspection.floor.room.entity.InspectionRoom.PassState;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PassStateConverter implements Converter<String, PassState> {
    @Override
    public PassState convert(String source) {
        return Arrays.stream(PassState.values())
                .filter(passState -> passState.getLabel().equals(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid value. No Matching Enum Constant. your input value ->  " + source));
    }
}
