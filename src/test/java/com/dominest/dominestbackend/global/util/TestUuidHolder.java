package com.dominest.dominestbackend.global.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestUuidHolder implements UuidHolder {
    private final String uuid;
    @Override
    public String random() {
        return uuid;
    }
}
