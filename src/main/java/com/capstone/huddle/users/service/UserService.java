package com.capstone.huddle.users.service;

import com.capstone.huddle.users.dto.request.UserRequest;
import com.capstone.huddle.users.dto.response.UserResponse;
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

    public UserResponse<UserEntity> signup(UserRequest userRequest) {
        log.info("Creating new user {}", userRequest.getUsername());

        UserEntity user = new UserEntity();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        UserEntity savedEntity = userRepository.save(user);

        return UserResponse.<UserEntity>builder()
                .status("success")
                .message("User created successfully")
                .data(savedEntity)
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
