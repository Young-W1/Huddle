package com.capstone.huddle.report.service;

import com.capstone.huddle.report.dto.request.CreateReportDto;
import com.capstone.huddle.report.dto.request.UpdateReportDto;
import com.capstone.huddle.report.entity.ReportEntity;
import com.capstone.huddle.report.entity.ReportStatus;
import com.capstone.huddle.report.repository.ReportRepository;
import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public ReportEntity createReport(CreateReportDto dto, String reporterUsername) {
        log.debug("Creating report for user: {} on article: {}", reporterUsername, dto.getArticleId());

        var reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new RuntimeException("Reporter not found: " + reporterUsername));

        var article = articleRepository.findById(dto.getArticleId())
                .orElseThrow(() -> new RuntimeException("Article not found: " + dto.getArticleId()));

        // Check if the user has already reported this article
        boolean existingReport = reportRepository.existsByReporterAndArticle(reporter, article);
        if (existingReport) {
            throw new RuntimeException("You have already reported this article");
        }

        var report = ReportEntity.builder()
                .reporter(reporter)
                .reportedUser(article.getAuthor())
                .article(article)
                .reason(dto.getReason())
//                .description(dto.getDescription())
                .status(ReportStatus.PENDING)
                .build();

        var savedReport = reportRepository.save(report);
        log.info("Report created with id: {}", savedReport.getId());

        return savedReport;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<ReportEntity> getAllReports(ReportStatus status, Pageable pageable) {
        log.debug("Fetching reports with status: {}", status);

        if (status != null) {
            return reportRepository.findByStatus(status, pageable);
        }
        return reportRepository.findAll(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ReportEntity updateReportStatus(UUID reportId, UpdateReportDto dto, String adminUsername) {
        log.debug("Updating report: {} by admin: {}", reportId, adminUsername);

        var report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));

        // Only allow status updates for pending reports
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new RuntimeException("Can only update pending reports. Current status: " + report.getStatus());
        }

        report.setStatus(dto.getStatus());
        report.setReviewNotes(dto.getAdminNotes());
        report.setReviewedBy(adminUsername);
        report.setReviewedAt(LocalDateTime.now());

        var updatedReport = reportRepository.save(report);
        log.info("Report {} updated to status: {}", reportId, dto.getStatus());

        return updatedReport;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ReportEntity getReportById(UUID reportId) {
        log.debug("Fetching report by id: {}", reportId);

        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));
    }

    public Page<ReportEntity> getReportsByReporter(String username, Pageable pageable) {
        log.debug("Fetching reports for reporter: {}", username);

        var reporter = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return reportRepository.findByReporter(reporter, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<ReportEntity> getReportsByReportedUser(String username, Pageable pageable) {
        log.debug("Fetching reports for reported user: {}", username);

        var reportedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return reportRepository.findByReportedUser(reportedUser, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<ReportEntity> getReportsByArticle(UUID articleId, Pageable pageable) {
        log.debug("Fetching reports for article: {}", articleId);

        var article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found: " + articleId));

        return reportRepository.findByArticle(article, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public long getReportCountByStatus(ReportStatus status) {
        if (status == null) {
            return reportRepository.count();
        }
        return reportRepository.countByStatus(status);
    }
}
