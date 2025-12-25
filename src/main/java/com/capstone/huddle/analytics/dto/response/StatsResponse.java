package com.capstone.huddle.analytics.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsResponse <T> {
    private boolean success;
    private String message;
    private T data;
}
