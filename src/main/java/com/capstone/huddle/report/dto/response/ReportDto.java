package com.capstone.huddle.report.dto.response;

import com.capstone.huddle.report.entity.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private UUID id;
    private String reporterUsername;
    private String reportedUsername;
    private UUID articleId;
    private String articleTitle;
    private String reason;
    private String description;
    private ReportStatus status;
    private String reviewedBy;
    private String reviewNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime reviewedAt;
}
