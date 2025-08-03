package com.capstone.huddle.users.controller;

import com.capstone.huddle.users.dto.request.LoginRequest;
import com.capstone.huddle.users.dto.request.UserRequest;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.service.UserService;
import com.capstone.huddle.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/huddle")
public class SignupController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> createNewUser(@Valid @RequestBody UserRequest userRequest) {
        userService.signup(userRequest);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        UserEntity user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user != null) {
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok("Login successful. Token: " + token);
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(Map.of(
                "username", username,
                "message", "Profile retrieved successfully"
        ));
    }

}
