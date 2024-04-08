package com.dominest.dominestbackend.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.Principal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrincipalParser {
    public static String toEmail(Principal principal) {
        return principal.getName().split(":")[0];
    }

    public static String toName(String principalStr) {
        String[] splitedPrincipal = principalStr.split(":");
        if (splitedPrincipal.length < 2) {
            return "anonymous";
        }
        return splitedPrincipal[1];
    }
}
