package com.dominest.dominestbackend.domain.resident;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ResidentTest {
    @DisplayName("그냥그냥")
    @Test
    void 그냥() {
        //given
        Resident resident = Resident.builder()
                .name("홍길동")
                .build();
        //when
        String name = resident.getPersonalInfo().getName();

        //then

    }


}
