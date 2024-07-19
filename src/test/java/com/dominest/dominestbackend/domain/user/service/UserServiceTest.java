package com.dominest.dominestbackend.domain.user.service;

import com.dominest.dominestbackend.api.user.request.JoinRequest;
import com.dominest.dominestbackend.domain.user.entity.User;
import com.dominest.dominestbackend.domain.user.repository.UserJpaRepository;
import com.dominest.dominestbackend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {

    @Autowired UserService userService;
    @Autowired UserJpaRepository userJpaRepository;
    @Autowired UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }

    @DisplayName("JoinRequest 객체로 회원가입을 할 수 있다")
    @Test
    void given_joinRequest_should_join() {
        //given
        JoinRequest request = new JoinRequest("email@example.com", "1234", "name", "010-0100-0100");

        //when
        User savedUser = userService.save(request);

        //then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail().getValue()).isEqualTo(request.getEmail());
        assertThat(savedUser.getPassword()).isNotEqualTo(request.getPassword());
        assertThat(savedUser.getName()).isEqualTo(request.getName());
        assertThat(savedUser.getPhoneNumber().getValue()).isEqualTo(request.getPhoneNumber());
    }
}
