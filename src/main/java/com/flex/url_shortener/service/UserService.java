package com.flex.url_shortener.service;

import com.flex.url_shortener.dto.UserRequest;
import com.flex.url_shortener.entity.Role;
import com.flex.url_shortener.entity.User;
import com.flex.url_shortener.exception.ValueDuplicationException;
import com.flex.url_shortener.mapper.UserMapper;
import com.flex.url_shortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        var probe = User.builder()
                .email(email)
                .build();
        return userRepository.findOne(Example.of(probe)).orElseThrow();
    }

    @Transactional
    public void signUp(UserRequest userRequest) {
        var user = userMapper.toEntity(userRequest);
        user.getRoles().add(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        var probe = User.builder()
                .email(user.getEmail())
                .build();

        if (userRepository.exists(Example.of(probe))) {
            throw new ValueDuplicationException("User with email %s already exists".formatted(user.getEmail()));
        }

        userRepository.save(user);
    }

}
