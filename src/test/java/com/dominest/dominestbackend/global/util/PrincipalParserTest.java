package com.dominest.dominestbackend.global.util;

import com.dominest.dominestbackend.global.config.security.SecurityConst;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.auth.security.AnonymousUserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PrincipalParserTest {

    @DisplayName("Principal에서 email을 추출할 수 있다")
    @Test
    void toEmail() {
        //given
        String name = "user";
        String email = "user@email.com";
        Principal principal = new
                UsernamePasswordAuthenticationToken(
                        email + SecurityConst.PRINCIPAL_DELIMITER + name, null);
        //when
        String resultEmail = PrincipalParser.toEmail(principal);
        //then
        assertThat(resultEmail).isEqualTo(email);
    }

    @DisplayName("Anonymous token으로부터는 email 추출 시 예외가 발생한다")
    @Test
    void toEmail_whenParseAnonymousToken() {
        //given
        Principal principal = new
                AnonymousAuthenticationToken(
                        "key"
                        , "anonymous"
                        , List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );
        //when-then
        assertThatThrownBy(() -> PrincipalParser.toEmail(principal))
                .isInstanceOf(AnonymousUserException.class)
                .hasMessage(ErrorCode.ANONYMOUS_USER.getMessage());
    }

    @DisplayName("Principal에서 이름을 추출할 수 있다")
    @Test
    void toName() {
        //given
        String name = "user";
        String email = "user@email.com";
        Principal principal = new
                UsernamePasswordAuthenticationToken(
                email + SecurityConst.PRINCIPAL_DELIMITER + name, null);
        //when
        String resultName = PrincipalParser.toName(principal.getName());
        //then
        assertThat(resultName).isEqualTo(name);
    }

    @DisplayName("Anonymous token으로부터는 name 추출 시 예외가 발생한다")
    @Test
    void toName_whenParseAnonymousToken() {
        //given
        Principal principal = new
                AnonymousAuthenticationToken(
                "key"
                , "anonymous"
                , List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );
        //when
        String resultName = PrincipalParser.toName(principal.getName());
        //then
        assertThat(resultName).isEqualTo("anonymous");
    }
}
