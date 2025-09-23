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
public class ReportStatsDto {

    private long totalReports;
    private long pendingReports;
    private long resolvedReports;
    private long dismissedReports;
    private long underReviewReports;
    private Map<String, Long> reportsByStatus;
    private double resolutionRate;
}
