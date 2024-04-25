package com.dominest.dominestbackend.global.converter;

import com.dominest.dominestbackend.domain.post.inspection.floor.room.entity.InspectionRoom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PassStateConverterTest {

    @DisplayName("String 값에서 PassState 객체로 변환할 수 있다")
    @Test
    void convert() {
        //given
        PassStateConverter passStateConverter = new PassStateConverter();
        String passStateString = InspectionRoom.PassState.NOT_PASSED.getLabel();
        //when
        InspectionRoom.PassState convertedResult = passStateConverter.convert(passStateString);
        //then
        assertThat(convertedResult).isEqualTo(InspectionRoom.PassState.NOT_PASSED);
    }

    @DisplayName("잘못된 String 값에서 PassState 객체로 변환 시 예외를 던진다")
    @Test
    void convert_whenInvalidString() {
        //given
        PassStateConverter passStateConverter = new PassStateConverter();
        String invalidSource = "잘못된 값";
        //when //then
        assertThatThrownBy(() -> passStateConverter.convert(invalidSource))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid value. No Matching Enum Constant. your input value ->  " + invalidSource);
    }
}
