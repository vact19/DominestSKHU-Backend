package com.dominest.dominestbackend.domain.user.repository;

import com.dominest.dominestbackend.domain.user.entity.User;

import java.util.List;

public interface UserRepository {
    User getByEmail(String email);
    User getByEmailFetchRoles(String email);
    User getByRefreshToken(String refreshToken);
    User save(User user);
    List<User> findAll();
}
