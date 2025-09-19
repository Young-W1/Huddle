package com.capstone.huddle.report.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportResponse <T>{

    private boolean success;
    private String message;
    private T data;
}
