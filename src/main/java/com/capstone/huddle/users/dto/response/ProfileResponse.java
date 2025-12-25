package com.capstone.huddle.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {

    private UUID userId;
    private String username;
    private String email;
    private String bio;
    private String profilePicture;
    private String location;
    private String website;
    private Long followersCount;
    private Long followingCount;
    private Long articlesCount;
    private Boolean isFollowing;
    private LocalDateTime joinedDate;
}
