package com.dominest.dominestbackend.domain.user;

import com.dominest.dominestbackend.domain.common.BaseEntity;
import com.dominest.dominestbackend.domain.common.wrapper.PhoneNumber;
import com.dominest.dominestbackend.domain.user.component.Role;
import com.dominest.dominestbackend.domain.common.wrapper.Email;
import com.dominest.dominestbackend.global.exception.ErrorCode;
import com.dominest.dominestbackend.global.exception.exceptions.auth.jwt.JwtAuthenticationException;
import com.dominest.dominestbackend.global.util.DateConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Embedded
    private PhoneNumber phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> roles = new ArrayList<>();

    private String refreshToken;
    private LocalDateTime tokenExp;

    @Builder
    private User(Email email, String password, String name, PhoneNumber phoneNumber, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public void updateRefreshTokenAndTokenExp(String refreshToken, LocalDateTime refreshTokenExp) {
        this.refreshToken = refreshToken;
        this.tokenExp = refreshTokenExp;
    }

    public void logout(LocalDateTime now){
        this.refreshToken = "";
        this.tokenExp = now;
    }

    public void validateRefreshTokenExp(LocalDateTime now) {
        boolean isTokenExpired = tokenExp.isBefore(now);
        if(isTokenExpired){
            throw new JwtAuthenticationException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // principal이 UserDetails 타입일 경우, principal.getName()의 반환값.
    @Override
    public String getUsername() {
        return email.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
