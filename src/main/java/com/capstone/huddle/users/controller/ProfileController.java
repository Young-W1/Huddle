package com.capstone.huddle.users.controller;

import com.capstone.huddle.users.dto.request.ProfileUpdateRequest;
import com.capstone.huddle.users.dto.response.ProfileResponse;
import com.capstone.huddle.users.dto.response.UserResponse;
import com.capstone.huddle.users.service.FollowService;
import com.capstone.huddle.users.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/huddle/users")
@RequiredArgsConstructor
@Tag(name = "Profile Management", description = "APIs for user profiles and follow/unfollow functionality")

public class ProfileController {

    private final FollowService followService;
    private final ProfileService profileService;

    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get User Profile", description = "Retrieve user profile information by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProfileResponse> getUserProfile(
            @PathVariable UUID userId,
            Principal principal) {
        String currentUsername = principal != null ? principal.getName() : null;
        ProfileResponse profile = profileService.getUserProfile(userId, currentUsername);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user's profile", description = "Update the profile information of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponse<ProfileResponse>> updateProfile(
            @RequestBody ProfileUpdateRequest request,
            Principal principal) {
        UserResponse<ProfileResponse> response = profileService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/follow")
    @Operation(summary = "Follow a user", description = "Follow another user by their userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User followed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - cannot follow yourself or already following"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User to follow not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponse<String>> followUser(
            @PathVariable UUID userId,
            Principal principal) {
        UserResponse<String> response = followService.followUser(principal.getName(), userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/unfollow")
    @Operation(summary = "Unfollow a user", description = "Unfollow a user by their userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User unfollowed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - cannot unfollow yourself or not already following"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User to unfollow not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponse<String>> unfollowUser(
            @PathVariable UUID userId,
            Principal principal) {
        UserResponse<String> response = followService.unfollowUser(principal.getName(), userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/followers")
    @Operation(summary = "Get user's followers",
            description = "Retrieve a paginated list of users who follow the specified user")
    @Parameter(name = "page", description = "Page number (0-indexed)", example = "0")
    @Parameter(name = "size", description = "Page size", example = "20")
    @Parameter(name = "sort", description = "Sort criteria (e.g., 'username,asc, follower.username,asc')", example = "followDate,desc")    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Followers retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<ProfileResponse>> getFollowers(
            @PathVariable UUID userId,
            Pageable pageable,
            Principal principal) {
        String currentUsername = principal != null ? principal.getName() : null;
        Page<ProfileResponse> followers = followService.getFollowers(userId, pageable, currentUsername);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    @Operation(summary = "Get users that this user is following", description = "Retrieve a paginated list of users that the specified user is following")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Following list retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<ProfileResponse>> getFollowing(
            @PathVariable UUID userId,
            Pageable pageable,
            Principal principal) {
        String currentUsername = principal != null ? principal.getName() : null;
        Page<ProfileResponse> following = followService.getFollowing(userId, pageable, currentUsername);
        return ResponseEntity.ok(following);
    }
}
