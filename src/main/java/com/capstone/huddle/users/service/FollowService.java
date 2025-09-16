package com.capstone.huddle.users.service;

import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.notifications.service.NotificationService;
import com.capstone.huddle.users.dto.response.ProfileResponse;
import com.capstone.huddle.users.dto.response.UserResponse;
import com.capstone.huddle.users.model.FollowEntity;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.FollowRepository;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ArticleRepository articleRepository;
    private final NotificationService notificationService;

    public UserResponse<String> followUser(String followerUsername, UUID followingUserId) {
        UserEntity follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        UserEntity following = userRepository.findById(followingUserId)
                .orElseThrow(() -> new RuntimeException("User to follow not found"));

        if (follower.getId().equals(following.getId())) {
            throw new RuntimeException("Cannot follow yourself");
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("Already following this user");
        }

        FollowEntity follow = FollowEntity.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        // Create notification for the followed user
        notificationService.createFollowNotification(follower, following);

        log.info("User {} successfully followed user {}", followerUsername, following.getUsername());

        return new UserResponse<>(true, "Successfully followed user", following.getUsername());
    }

    public UserResponse<String> unfollowUser(String followerUsername, UUID followingUserId) {
        UserEntity follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        UserEntity following = userRepository.findById(followingUserId)
                .orElseThrow(() -> new RuntimeException("User to unfollow not found"));

        FollowEntity follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new RuntimeException("Not following this user"));

        followRepository.delete(follow);

        return new UserResponse<>(true, "Successfully unfollowed user", following.getUsername());
    }

    public Page<ProfileResponse> getFollowers(UUID userId, Pageable pageable, String currentUsername) {
        return followRepository.findFollowersByUserId(userId, pageable)
                .map(follow -> mapToProfileResponse(follow.getFollower(), currentUsername));
    }

    public Page<ProfileResponse> getFollowing(UUID userId, Pageable pageable, String currentUsername) {
        return followRepository.findFollowingByUserId(userId, pageable)
                .map(follow -> mapToProfileResponse(follow.getFollowing(), currentUsername));
    }

    private ProfileResponse mapToProfileResponse(UserEntity user, String currentUsername) {
        UserEntity currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        boolean isFollowing = currentUser != null &&
                followRepository.existsByFollowerAndFollowing(currentUser, user);

        long articlesCount = articleRepository.countByAuthor(user);

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
                .articlesCount(articlesCount)
                .isFollowing(isFollowing)
                .joinedDate(user.getCreatedAt())
                .build();
    }

}
