package com.dominest.dominestbackend.global.util;

import com.dominest.dominestbackend.global.config.security.SecurityConst;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.auth.security.AnonymousUserException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

import java.security.Principal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrincipalParser {
    public static String toEmail(Principal principal) {
        if (principal instanceof AnonymousAuthenticationToken)
            throw new AnonymousUserException(ErrorCode.ANONYMOUS_USER);
        return principal.getName().split(SecurityConst.PRINCIPAL_DELIMITER)[0];
    }

    // 데이터베이스에 이미 저장된 Principal String을 파싱할 때 사용된다.
    public static String toName(String principalStr) {
        String[] splitPrincipal = principalStr.split(SecurityConst.PRINCIPAL_DELIMITER);
        boolean isAnonymous = splitPrincipal.length < 2;
        if (isAnonymous) {
            return "anonymous";
        }
        return splitPrincipal[1];
    }
}
