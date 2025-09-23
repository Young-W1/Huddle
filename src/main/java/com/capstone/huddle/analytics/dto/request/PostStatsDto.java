package com.capstone.huddle.analytics.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostStatsDto {

    private long totalPosts;
    private long postsLast7Days;
    private Map<String, Long> postsByCategory;
    private Map<String, Long> topContributors; // username -> post count
    private double averagePostsPerUser;
}
