package com.capstone.huddle.report.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class CreateReportDto {
    @NotNull
    private UUID articleId;

//    private UUID reportedUserId; // fallback (direct user report)

    @NotBlank
    private String reason;
}