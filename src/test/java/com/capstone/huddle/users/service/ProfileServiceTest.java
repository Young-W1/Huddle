package com.capstone.huddle.users.service;

import com.capstone.huddle.users.dto.request.ProfileUpdateRequest;
import com.capstone.huddle.users.dto.response.ProfileResponse;
import com.capstone.huddle.users.dto.response.UserResponse;
import com.capstone.huddle.users.model.ProfileEntity;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.FollowRepository;
import com.capstone.huddle.users.repository.ProfileRepository;
import com.capstone.huddle.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private ProfileService profileService;

    private UserEntity user1;
    private UserEntity user2;
    private ProfileEntity profile1;
    private UUID userId1;
    private UUID userId2;

    @BeforeEach
    void setUp() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();

        profile1 = ProfileEntity.builder()
                .bio("Original bio")
                .profilePicture("original.jpg")
                .location("Original Location")
                .website("http://original.com")
                .build();

        user1 = UserEntity.builder()
                .id(userId1)
                .username("user1")
                .email("user1@example.com")
                .profile(profile1)
                .createdAt(LocalDateTime.now())
                .build();

        user2 = UserEntity.builder()
                .id(userId2)
                .username("user2")
                .email("user2@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        profile1.setUser(user1);
    }

    @Test
    void userCanUpdateOwnProfile() {
        // Given
        ProfileUpdateRequest updateRequest = ProfileUpdateRequest.builder()
                .bio("New bio")
                .profilePicture("new.jpg")
                .location("New Location")
                .website("http://new.com")
                .build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(profileRepository.save(any(ProfileEntity.class))).thenReturn(profile1);
        when(followRepository.countFollowers(user1)).thenReturn(5L);
        when(followRepository.countFollowing(user1)).thenReturn(3L);

        // When
        UserResponse<ProfileResponse> response = profileService.updateProfile("user1", updateRequest);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Profile updated successfully", response.getMessage());
        verify(profileRepository).save(profile1);
        assertEquals("New bio", profile1.getBio());
        assertEquals("new.jpg", profile1.getProfilePicture());
        assertEquals("New Location", profile1.getLocation());
        assertEquals("http://new.com", profile1.getWebsite());
    }

    @Test
    void cannotUpdateNonExistentUserProfile() {
        // Given
        ProfileUpdateRequest updateRequest = ProfileUpdateRequest.builder()
                .bio("New bio")
                .build();

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                profileService.updateProfile("nonexistent", updateRequest)
        );

        assertEquals("User not found", exception.getMessage());
        verify(profileRepository, never()).save(any());
    }

    @Test
    void createProfileIfNotExists() {
        // Given
        user2.setProfile(null); // User without profile
        ProfileUpdateRequest updateRequest = ProfileUpdateRequest.builder()
                .bio("First bio")
                .profilePicture("first.jpg")
                .build();

        when(userRepository.findByUsername("user2")).thenReturn(Optional.of(user2));
        when(profileRepository.save(any(ProfileEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(followRepository.countFollowers(user2)).thenReturn(0L);
        when(followRepository.countFollowing(user2)).thenReturn(0L);

        // When
        UserResponse<ProfileResponse> response = profileService.updateProfile("user2", updateRequest);

        // Then
        assertTrue(response.isSuccess());
        assertNotNull(user2.getProfile());
        assertEquals("First bio", user2.getProfile().getBio());
        assertEquals("first.jpg", user2.getProfile().getProfilePicture());
        verify(profileRepository).save(any(ProfileEntity.class));
    }

    @Test
    void getUserProfileShowsCorrectFollowStatus() {
        // Given
        when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(followRepository.existsByFollowerAndFollowing(user1, user2)).thenReturn(true);
        when(followRepository.countFollowers(user2)).thenReturn(10L);
        when(followRepository.countFollowing(user2)).thenReturn(5L);

        // When
        ProfileResponse profile = profileService.getUserProfile(userId2, "user1");

        // Then
        assertEquals(userId2, profile.getUserId());
        assertEquals("user2", profile.getUsername());
        assertTrue(profile.getIsFollowing());
        assertEquals(10L, profile.getFollowersCount());
        assertEquals(5L, profile.getFollowingCount());
    }

    @Test
    void getUserProfileWhenNotLoggedIn() {
        // Given
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(followRepository.countFollowers(user1)).thenReturn(5L);
        when(followRepository.countFollowing(user1)).thenReturn(3L);

        // When
        ProfileResponse profile = profileService.getUserProfile(userId1, null);

        // Then
        assertEquals(userId1, profile.getUserId());
        assertFalse(profile.getIsFollowing());
        verify(followRepository, never()).existsByFollowerAndFollowing(any(), any());
    }
}