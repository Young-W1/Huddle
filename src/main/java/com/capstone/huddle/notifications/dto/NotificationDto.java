package com.capstone.huddle.notifications.dto;

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
public class NotificationDto {
    private UUID id;
    private String actorUsername;
    private String actorProfilePicture;
    private String type;
    private String message;
    private UUID entityId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}