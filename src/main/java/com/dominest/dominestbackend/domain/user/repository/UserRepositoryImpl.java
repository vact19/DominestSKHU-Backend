package com.dominest.dominestbackend.domain.user.repository;

import com.dominest.dominestbackend.domain.common.Datasource;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.global.exception.exceptions.external.db.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return userJpaRepository.findByEmailValue(email)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.USER, "email", email));
    }

    @Override
    public User getByEmailFetchRoles(String email) {
        return userJpaRepository.findByEmailFetchRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다."));
    }

    @Override
    public User getByRefreshToken(String refreshToken) {
        return userJpaRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException(Datasource.USER
                        , "refreshToken", refreshToken));
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }
}
