package com.capstone.huddle.users.service;

import com.capstone.huddle.users.dto.request.ProfileUpdateRequest;
import com.capstone.huddle.users.dto.response.ProfileResponse;
import com.capstone.huddle.users.dto.response.UserResponse;
import com.capstone.huddle.users.model.ProfileEntity;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.FollowRepository;
import com.capstone.huddle.users.repository.ProfileRepository;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final FollowRepository followRepository;

    public ProfileResponse getUserProfile(UUID userId, String currentUsername) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserEntity currentUser = currentUsername != null ?
                userRepository.findByUsername(currentUsername).orElse(null) : null;

        boolean isFollowing = currentUser != null &&
                followRepository.existsByFollowerAndFollowing(currentUser, user);

        return ProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getProfile() != null ? user.getProfile().getBio() : null)
                .profilePicture(user.getProfile() != null ? user.getProfile().getProfilePicture() : null)
                .location(user.getProfile() != null ? user.getProfile().getLocation() : null)
                .website(user.getProfile() != null ? user.getProfile().getWebsite() : null)
                .followersCount(followRepository.countFollowers(user))
                .followingCount(followRepository.countFollowing(user))
                .articlesCount(0L) // TODO: Add article count logic
                .isFollowing(isFollowing)
                .joinedDate(user.getCreatedAt())
                .build();
    }

    public UserResponse<ProfileResponse> updateProfile(String username, ProfileUpdateRequest request) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProfileEntity profile = user.getProfile();
        if (profile == null) {
            profile = ProfileEntity.builder()
                    .user(user)
                    .build();
            user.setProfile(profile);
        }

        profile.setBio(request.getBio());
        profile.setProfilePicture(request.getProfilePicture());
        profile.setLocation(request.getLocation());
        profile.setWebsite(request.getWebsite());

        profileRepository.save(profile);

        ProfileResponse response = getUserProfile(user.getId(), username);
        return new UserResponse<>(true, "Profile updated successfully", response);
    }
}
