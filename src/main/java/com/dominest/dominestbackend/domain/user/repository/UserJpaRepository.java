package com.dominest.dominestbackend.domain.user.repository;

import com.dominest.dominestbackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailValue(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailFetchRoles(@Param("email") String email);

    Optional<User> findByRefreshToken(String refreshToken);
}
