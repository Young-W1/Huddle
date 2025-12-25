package com.capstone.huddle.report.service;

import com.capstone.huddle.report.dto.request.CreateReportDto;
import com.capstone.huddle.report.dto.request.UpdateReportDto;
import com.capstone.huddle.report.dto.response.ReportResponseDto;
import com.capstone.huddle.report.entity.ReportEntity;
import com.capstone.huddle.report.entity.ReportStatus;
import com.capstone.huddle.report.repository.ReportRepository;
import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public ReportResponseDto createReport(CreateReportDto dto, String reporterUsername) {
        var reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new EntityNotFoundException("Reporter not found"));

        var article = articleRepository.findById(dto.getArticleId())
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        var author = article.getAuthor();
        if (author == null) {
            throw new IllegalStateException("Article has no author");
        }

        var report = new ReportEntity();
        report.setReporter(reporter);
        report.setReportedUser(author);
        report.setReportedArticle(article);
        report.setReason(dto.getReason());
        report.setStatus(ReportStatus.PENDING);
        // createdAt handled by @PrePersist

        var saved = reportRepository.save(report);
        log.info("Report created: {} by user {}", saved.getId(), reporterUsername);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReportResponseDto> getAllReports(ReportStatus status, Pageable pageable) {
        Page<ReportEntity> page = (status != null)
                ? reportRepository.findByStatus(status, pageable)
                : reportRepository.findAll(pageable);
        return page.map(this::toDto);
    }

    @Transactional
    public ReportResponseDto updateReportStatus(UUID reportId, UpdateReportDto dto, String adminUsername) {
        var report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        var admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

        report.setStatus(dto.getStatus());
        report.setAdminNotes(dto.getAdminNotes());
        report.setResolvedBy(admin);
        report.setResolvedAt(LocalDateTime.now());

        var saved = reportRepository.save(report);
        log.info("Report {} updated to status {} by admin {}", reportId, dto.getStatus(), adminUsername);
        return toDto(saved);
    }

    private ReportResponseDto toDto(ReportEntity r) {
        return ReportResponseDto.builder()
                .id(r.getId())
                .status(r.getStatus().name())
                .reason(r.getReason())
                .reporterId(r.getReporter().getId())
                .reporterUsername(r.getReporter().getUsername())
                .reportedUserId(r.getReportedUser() != null ? r.getReportedUser().getId() : null)
                .reportedUsername(r.getReportedUser() != null ? r.getReportedUser().getUsername() : null)
                .articleId(r.getReportedArticle() != null ? r.getReportedArticle().getId() : null)
                .articleTitle(r.getReportedArticle() != null ? r.getReportedArticle().getTitle() : null)
                .createdAt(r.getCreatedAt())
                .build();
    }
}
