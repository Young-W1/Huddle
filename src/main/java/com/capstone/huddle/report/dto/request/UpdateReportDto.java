package com.capstone.huddle.report.dto.request;

import com.capstone.huddle.report.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReportDto {
    @NotNull
    private ReportStatus status;
    private String adminNotes;

}
