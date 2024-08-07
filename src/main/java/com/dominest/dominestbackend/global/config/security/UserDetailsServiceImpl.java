package com.dominest.dominestbackend.global.config.security;

import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 사용자 정보를 이메일로 조회
        User user = userRepository.getByEmailFetchRoles(email);

        // Spring Security의 UserDetails로 변환하여 반환
        return new org.springframework.security.core.userdetails.User(
                user.getEmail().getValue(),
                user.getPassword(),
                user.getAuthorities()
        );
    }
}
