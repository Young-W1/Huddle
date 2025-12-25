package com.capstone.huddle.report.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReportResponseDto {
    private UUID id;
    private String status;
    private String reason;
    private UUID reporterId;
    private String reporterUsername;
    private UUID reportedUserId;
    private String reportedUsername;
    private UUID articleId;
    private String articleTitle;
    private LocalDateTime createdAt;
}
