package com.capstone.huddle.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFilterDto {
    private String searchTerm;
    private String notificationType;
    private Boolean isRead;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
