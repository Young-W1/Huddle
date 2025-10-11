package com.capstone.huddle.analytics.service;

import com.capstone.huddle.analytics.dto.request.PostStatsDto;
import com.capstone.huddle.analytics.dto.request.ReportStatsDto;
import com.capstone.huddle.analytics.dto.request.UserStatsDto;
import com.capstone.huddle.analytics.dto.response.AnalyticsResponseDto;
import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.report.entity.ReportStatus;
import com.capstone.huddle.report.repository.ReportRepository;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ReportRepository reportRepository;

    @Transactional(readOnly = true)
    public PostStatsDto getPostStats() {
        // Get total posts
        long totalPosts = articleRepository.countTotalArticles();

        // Get posts by category
        List<Object[]> categoryResults = articleRepository.countArticlesByCategory();
        Map<String, Long> postsByCategory = new HashMap<>();
        for (Object[] result : categoryResults) {
            if (result[0] != null) {
                postsByCategory.put(result[0].toString(), (Long) result[1]);
            }
        }

        // Get top 5 contributors (reduced from 10)
        Pageable topFive = PageRequest.of(0, 5);
        List<Object[]> contributorResults = articleRepository.getTopContributors(topFive);
        Map<String, Long> topContributors = new LinkedHashMap<>(); // LinkedHashMap to maintain order
        for (Object[] result : contributorResults) {
            topContributors.put((String) result[0], (Long) result[1]);
        }

        return PostStatsDto.builder()
                .totalPosts(totalPosts)
                .postsByCategory(postsByCategory)
                .topContributors(topContributors)
                .build();
    }

    @Transactional(readOnly = true)
    public UserStatsDto getUserStats() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        long totalUsers = userRepository.count();
        long newUsersThisWeek = userRepository.countUsersCreatedAfter(oneWeekAgo);
        long activeUsersLast30Days = userRepository.countActiveUsersSince(thirtyDaysAgo);

        double growthRate = 0.0;
        if (totalUsers > newUsersThisWeek) {
            growthRate = ((double) newUsersThisWeek / (totalUsers - newUsersThisWeek)) * 100;
        }

        return UserStatsDto.builder()
                .totalUsers(totalUsers)
                .newUsersThisWeek(newUsersThisWeek)
                .activeUsersLast30Days(activeUsersLast30Days)
                .userGrowthRate(Math.round(growthRate * 100.0) / 100.0)
                .build();
    }


    @Transactional(readOnly = true)
    public ReportStatsDto getReportStats() {
        long totalReports = reportRepository.count();
        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING);
        long resolvedReports = reportRepository.countByStatus(ReportStatus.RESOLVED);
        long dismissedReports = reportRepository.countByStatus(ReportStatus.DISMISSED);
        long underReviewReports = reportRepository.countByStatus(ReportStatus.UNDER_REVIEW);

        // Get reports by status
        List<Object[]> statusResults = reportRepository.countReportsByStatus();
        Map<String, Long> reportsByStatus = new HashMap<>();
        for (Object[] result : statusResults) {
            reportsByStatus.put(result[0].toString(), (Long) result[1]);
        }

        double resolutionRate = 0.0;
        if (totalReports > 0) {
            resolutionRate = ((double) (resolvedReports + dismissedReports) / totalReports) * 100;
        }

        return ReportStatsDto.builder()
                .totalReports(totalReports)
                .pendingReports(pendingReports)
                .resolvedReports(resolvedReports)
                .dismissedReports(dismissedReports)
                .underReviewReports(underReviewReports)
                .reportsByStatus(reportsByStatus)
                .resolutionRate(Math.round(resolutionRate * 100.0) / 100.0)
                .build();
    }


    @Transactional(readOnly = true)
    public AnalyticsResponseDto getAllAnalytics() {
        return AnalyticsResponseDto.builder()
                .userStats(getUserStats())
                .postStats(getPostStats())
                .reportStats(getReportStats())
                .build();
    }
}
