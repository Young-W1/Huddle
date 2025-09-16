package com.capstone.huddle.users.service;

import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.users.dto.response.ProfileResponse;
import com.capstone.huddle.users.dto.response.UserResponse;
import com.capstone.huddle.users.model.FollowEntity;
import com.capstone.huddle.users.model.ProfileEntity;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.FollowRepository;
import com.capstone.huddle.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private FollowService followService;

    private UserEntity user1;
    private UserEntity user2;
    private UUID userId1;
    private UUID userId2;

    @BeforeEach
    void setUp() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();

        user1 = UserEntity.builder()
                .id(userId1)
                .username("user1")
                .email("user1@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        user2 = UserEntity.builder()
                .id(userId2)
                .username("user2")
                .email("user2@example.com")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void userCannotFollowThemself() {
        // Given
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                followService.followUser("user1", userId1)
        );

        assertEquals("Cannot follow yourself", exception.getMessage());
        verify(followRepository, never()).save(any(FollowEntity.class));
    }

    @Test
    void userCanFollowAnotherUser() {
        // Given
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));
        when(followRepository.existsByFollowerAndFollowing(user1, user2)).thenReturn(false);

        // When
        UserResponse<String> response = followService.followUser("user1", userId2);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Successfully followed user", response.getMessage());
        assertEquals("user2", response.getData());
        verify(followRepository).save(any(FollowEntity.class));
    }

    @Test
    void userCannotFollowSameUserTwice() {
        // Given
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));
        when(followRepository.existsByFollowerAndFollowing(user1, user2)).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                followService.followUser("user1", userId2)
        );

        assertEquals("Already following this user", exception.getMessage());
        verify(followRepository, never()).save(any(FollowEntity.class));
    }

    @Test
    void userCanUnfollowAnotherUser() {
        // Given
        FollowEntity followEntity = FollowEntity.builder()
                .follower(user1)
                .following(user2)
                .build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));
        when(followRepository.findByFollowerAndFollowing(user1, user2))
                .thenReturn(Optional.of(followEntity));

        // When
        UserResponse<String> response = followService.unfollowUser("user1", userId2);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Successfully unfollowed user", response.getMessage());
        verify(followRepository).delete(followEntity);
    }

    @Test
    void cannotUnfollowUserNotBeingFollowed() {
        // Given
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));
        when(followRepository.findByFollowerAndFollowing(user1, user2))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                followService.unfollowUser("user1", userId2)
        );

        assertEquals("Not following this user", exception.getMessage());
    }

    @Test
    void getFollowersReturnsCorrectData() {
        // Given
        ProfileEntity profile = ProfileEntity.builder()
                .bio("Test bio")
                .profilePicture("pic.jpg")
                .build();
        user1.setProfile(profile);

        FollowEntity follow = FollowEntity.builder()
                .follower(user1)
                .following(user2)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<FollowEntity> followPage = new PageImpl<>(List.of(follow));

        when(followRepository.findFollowersByUserId(userId2, pageable)).thenReturn(followPage);
        when(userRepository.findByUsername("currentUser")).thenReturn(Optional.of(user1));
        when(followRepository.existsByFollowerAndFollowing(user1, user1)).thenReturn(false);
        when(followRepository.countFollowers(user1)).thenReturn(5L);
        when(followRepository.countFollowing(user1)).thenReturn(3L);
        when(articleRepository.countByAuthor(user1)).thenReturn(10L);

        // When
        Page<ProfileResponse> followers = followService.getFollowers(userId2, pageable, "currentUser");

        // Then
        assertEquals(1, followers.getTotalElements());
        ProfileResponse followerProfile = followers.getContent().get(0);
        assertEquals("user1", followerProfile.getUsername());
        assertEquals("Test bio", followerProfile.getBio());
        assertEquals("pic.jpg", followerProfile.getProfilePicture());
        assertEquals(5L, followerProfile.getFollowersCount());
        assertEquals(3L, followerProfile.getFollowingCount());
        assertEquals(10L, followerProfile.getArticlesCount());
    }
}