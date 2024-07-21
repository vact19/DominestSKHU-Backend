package com.dominest.dominestbackend.global.util.mock;

import com.dominest.dominestbackend.global.util.ClockHolder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class FixedClockHolder implements ClockHolder {

    private final LocalDateTime fixedTime;

    @Override
    public LocalDateTime now() {
        return fixedTime;
    }
}
