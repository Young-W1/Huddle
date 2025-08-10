package com.capstone.huddle.users.controller;

import com.capstone.huddle.users.dto.request.LoginRequest;
import com.capstone.huddle.users.dto.request.UserRequest;
import com.capstone.huddle.users.dto.response.UserResponse;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.service.UserService;
import com.capstone.huddle.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Management", description = "APIs for user registration and authentication")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    @Operation(summary = "User Signup", description = "Create a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createNewUser(@Valid @RequestBody UserRequest userRequest) {
        try {
            UserResponse<UserEntity> response = userService.signup(userRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating user: ", e);
            UserResponse<Object> errorResponse = UserResponse.builder()
                    .status("400")
                    .message("Failed to create user: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }


//        userService.signup(userRequest);
//        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
    @Operation(summary = "Get User Profile", description = "Retrieve user profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(Map.of(
                "username", username,
                "message", "Profile retrieved successfully"
        ));
    }

}
