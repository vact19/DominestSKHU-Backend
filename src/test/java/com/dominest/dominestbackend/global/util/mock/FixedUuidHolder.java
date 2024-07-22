package com.dominest.dominestbackend.global.util.mock;

import com.dominest.dominestbackend.global.util.UuidHolder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FixedUuidHolder implements UuidHolder {
    private final String uuid;
    @Override
    public String random() {
        return uuid;
    }
}
