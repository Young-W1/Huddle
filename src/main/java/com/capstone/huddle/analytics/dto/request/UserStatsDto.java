package com.capstone.huddle.analytics.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDto {

    private long totalUsers;
    private long newUsersLast7Days;
    private long activeUsersLast30Days;
    private double userGrowthRate;
    private long newUsersThisWeek;
}
