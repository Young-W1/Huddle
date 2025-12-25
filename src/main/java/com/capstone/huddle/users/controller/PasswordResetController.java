package com.capstone.huddle.users.controller;

import com.capstone.huddle.common.ApiResponse;
import com.capstone.huddle.users.dto.request.PasswordResetConfirmRequest;
import com.capstone.huddle.users.dto.request.PasswordResetRequest;
import com.capstone.huddle.users.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/huddle/password")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Password Reset", description = "Password reset management APIs")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    @Operation(summary = "Request password reset", description = "Send a password reset link to the user's email")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset email sent (if account exists)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        try {
            String message = passwordResetService.initiatePasswordReset(request);
            return ResponseEntity.ok(ApiResponse.success(message, null));
        } catch (Exception e) {
            log.error("Error initiating password reset: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to process password reset request"));
        }
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset password", description = "Reset password using the token received via email")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password successfully reset"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid token or passwords don't match")
    })
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody PasswordResetConfirmRequest request) {
        try {
            passwordResetService.resetPassword(request);
            return ResponseEntity.ok(ApiResponse.success("Password successfully reset. You can now login with your new password.", null));
        } catch (Exception e) {
            log.error("Error resetting password: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/validate-token")
    @Operation(summary = "Validate reset token", description = "Check if a password reset token is valid")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token validation result")
    })
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> validateToken(@RequestParam String token) {
        boolean isValid = passwordResetService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success(
                isValid ? "Token is valid" : "Token is invalid or expired",
                Map.of("valid", isValid)
        ));
    }
}

