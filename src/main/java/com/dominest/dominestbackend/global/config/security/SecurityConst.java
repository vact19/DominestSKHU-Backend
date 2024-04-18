package com.dominest.dominestbackend.global.config.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConst {
    // Principal name은 email + ":" + name 으로 구성된다. 이는 JWT의 audience에도 들어간다.
    public static final String PRINCIPAL_DELIMITER = ":";
}
