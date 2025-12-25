package com.capstone.huddle.report.dto.request;

import com.capstone.huddle.report.entity.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReportDto {

    @NotNull(message = "Status is required")
    @Schema(description = "Report status", example = "RESOLVED", allowableValues = {"PENDING", "UNDER_REVIEW", "RESOLVED", "DISMISSED"})
    private ReportStatus status;

    @Schema(description = "Admin notes about the resolution", example = "Article violated community guidelines and has been removed")
    private String adminNotes;
}
