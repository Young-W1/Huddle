package com.capstone.huddle.users.service;

import com.capstone.huddle.users.dto.request.UserRequest;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserRequest signup(UserRequest userRequest) {
        log.info("Creating new user {}", userRequest.getUsername());
        UserEntity user = new UserEntity();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        UserEntity savedEntity = userRepository.save(user);

        return UserRequest.builder()
                .firstName(savedEntity.getFirstName())
                .lastName(savedEntity.getLastName())
                .username(savedEntity.getUsername())
                .email(savedEntity.getEmail())
                .password(savedEntity.getPassword())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public UserEntity login(String username, String password) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }


}
