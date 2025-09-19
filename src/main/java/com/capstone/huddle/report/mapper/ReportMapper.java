package com.capstone.huddle.report.mapper;

import com.capstone.huddle.report.dto.response.ReportDto;
import com.capstone.huddle.report.entity.ReportEntity;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportDto toDto(ReportEntity entity) {
        if (entity == null) return null;

        return ReportDto.builder()
                .id(entity.getId())
                .reporterUsername(entity.getReporter() != null ? entity.getReporter().getUsername() : null)
                .reportedUsername(entity.getReportedUser() != null ? entity.getReportedUser().getUsername() : null)
                .articleId(entity.getArticle() != null ? entity.getArticle().getId() : null)
                .articleTitle(entity.getArticle() != null ? entity.getArticle().getTitle() : null)
                .reason(entity.getReason())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .reviewedBy(entity.getReviewedBy())
                .reviewNotes(entity.getReviewNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .reviewedAt(entity.getReviewedAt())
                .build();
    }
}
