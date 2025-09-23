package com.capstone.huddle.analytics.controller;

import com.capstone.huddle.analytics.dto.request.PostStatsDto;
import com.capstone.huddle.analytics.dto.request.ReportStatsDto;
import com.capstone.huddle.analytics.dto.request.UserStatsDto;
import com.capstone.huddle.analytics.dto.response.AnalyticsResponseDto;
import com.capstone.huddle.analytics.service.AnalyticsService;
import com.capstone.huddle.report.dto.response.ReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/huddle/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics and statistics APIs")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all analytics", description = "Get comprehensive analytics including users, posts, and reports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved analytics"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin access required")
    })
    public ResponseEntity<?> getAllAnalytics() {
        try {
            AnalyticsResponseDto analytics = analyticsService.getAllAnalytics();

            ReportResponse<AnalyticsResponseDto> response = ReportResponse.<AnalyticsResponseDto>builder()
                    .success(true)
                    .message("Analytics retrieved successfully")
                    .data(analytics)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving analytics: ", e);
            ReportResponse<Object> errorResponse = ReportResponse.builder()
                    .success(false)
                    .message("Failed to retrieve analytics: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get user statistics", description = "Get user-related statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user stats"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin access required")
    })
    public ResponseEntity<?> getUserStats() {
        try {
            UserStatsDto userStats = analyticsService.getUserStats();

            ReportResponse<UserStatsDto> response = ReportResponse.<UserStatsDto>builder()
                    .success(true)
                    .message("User statistics retrieved successfully")
                    .data(userStats)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving user stats: ", e);
            ReportResponse<Object> errorResponse = ReportResponse.builder()
                    .success(false)
                    .message("Failed to retrieve user statistics: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/posts")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get post statistics", description = "Get post/article-related statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved post stats"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin access required")
    })
    public ResponseEntity<?> getPostStats() {
        try {
            PostStatsDto postStats = analyticsService.getPostStats();

            ReportResponse<PostStatsDto> response = ReportResponse.<PostStatsDto>builder()
                    .success(true)
                    .message("Post statistics retrieved successfully")
                    .data(postStats)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving post stats: ", e);
            ReportResponse<Object> errorResponse = ReportResponse.builder()
                    .success(false)
                    .message("Failed to retrieve post statistics: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/reports")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get report statistics", description = "Get report-related statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved report stats"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin access required")
    })
    public ResponseEntity<?> getReportStats() {
        try {
            ReportStatsDto reportStats = analyticsService.getReportStats();

            ReportResponse<ReportStatsDto> response = ReportResponse.<ReportStatsDto>builder()
                    .success(true)
                    .message("Report statistics retrieved successfully")
                    .data(reportStats)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving report stats: ", e);
            ReportResponse<Object> errorResponse = ReportResponse.builder()
                    .success(false)
                    .message("Failed to retrieve report statistics: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
