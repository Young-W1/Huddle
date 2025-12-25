package com.capstone.huddle.analytics.dto.response;

import com.capstone.huddle.analytics.dto.request.PostStatsDto;
import com.capstone.huddle.analytics.dto.request.ReportStatsDto;
import com.capstone.huddle.analytics.dto.request.UserStatsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsResponseDto {

    private UserStatsDto userStats;
    private PostStatsDto postStats;
    private ReportStatsDto reportStats;
}
