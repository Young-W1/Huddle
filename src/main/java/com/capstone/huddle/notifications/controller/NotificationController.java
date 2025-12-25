package com.capstone.huddle.notifications.controller;

import com.capstone.huddle.notifications.dto.NotificationDto;
import com.capstone.huddle.notifications.dto.NotificationFilterDto;
import com.capstone.huddle.notifications.dto.response.NotificationResponse;
import com.capstone.huddle.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

@Slf4j
@RestController
@RequestMapping("/huddle/notifications")
@Tag(name = "Notifications", description = "APIs for managing user notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get user notifications", description = "Retrieve paginated notifications for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationResponse<Page<NotificationDto>>> getNotifications(
            Principal principal,
            Pageable pageable) {
        try {
            String username = principal.getName();
            Page<NotificationDto> notifications = notificationService.getUserNotifications(username, pageable);

            return ResponseEntity.ok(NotificationResponse.<Page<NotificationDto>>builder()
                    .success(true)
                    .message("Notifications retrieved successfully")
                    .data(notifications)
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving notifications: ", e);
            return ResponseEntity.internalServerError().body(
                    NotificationResponse.<Page<NotificationDto>>builder()
                            .success(false)
                            .message("Failed to retrieve notifications: " + e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count", description = "Retrieve the count of unread notifications for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved unread count"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationResponse<Long>> getUnreadCount(Principal principal) {
        try {
            String username = principal.getName();
            Long unreadCount = notificationService.getUnreadCount(username);

            return ResponseEntity.ok(NotificationResponse.<Long>builder()
                    .success(true)
                    .message("Unread count retrieved successfully")
                    .data(unreadCount)
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving unread count: ", e);
            return ResponseEntity.internalServerError().body(
                    NotificationResponse.<Long>builder()
                            .success(false)
                            .message("Failed to retrieve unread count: " + e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    @PostMapping("/mark-all-read")
    @Operation(summary = "Mark all notifications as read", description = "Mark all notifications as read for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully marked all notifications as read"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationResponse<Void>> markAllAsRead(Principal principal) {
        try {
            String username = principal.getName();
            notificationService.markAllAsRead(username);

            return ResponseEntity.ok(NotificationResponse.<Void>builder()
                    .success(true)
                    .message("All notifications marked as read")
                    .data(null)
                    .build());
        } catch (Exception e) {
            log.error("Error marking notifications as read: ", e);
            return ResponseEntity.internalServerError().body(
                    NotificationResponse.<Void>builder()
                            .success(false)
                            .message("Failed to mark notifications as read: " + e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    @PostMapping("/mark-read/{notificationId}")
    @Operation(summary = "Mark a notification as read", description = "Mark a specific notification as read for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully marked the notification as read"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationResponse<Void>> markAsRead(
            Principal principal,
            @PathVariable UUID notificationId) {
        try {
            String username = principal.getName();
            notificationService.markAsRead(username, notificationId);
            return ResponseEntity.ok(NotificationResponse.<Void>builder()
                    .success(true)
                    .message("Notification marked as read")
                    .data(null)
                    .build());
        } catch (Exception e) {
            log.error("Error marking notification as read: ", e);
            return ResponseEntity.internalServerError().body(
                    NotificationResponse.<Void>builder()
                            .success(false)
                            .message("Failed to mark notification as read: " + e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter notifications",
            description = "Search notifications by content and filter by type, date range, and read status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered notifications"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationResponse<Page<NotificationDto>>> searchNotifications(
            Principal principal,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String notificationType,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        try {
            String username = principal.getName();

            NotificationFilterDto filter = NotificationFilterDto.builder()
                    .searchTerm(searchTerm)
                    .notificationType(notificationType)
                    .isRead(isRead)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            Page<NotificationDto> notifications = notificationService.searchNotifications(username, filter, pageable);

            return ResponseEntity.ok(NotificationResponse.<Page<NotificationDto>>builder()
                    .success(true)
                    .message("Filtered notifications retrieved successfully")
                    .data(notifications)
                    .build());
        } catch (Exception e) {
            log.error("Error searching notifications: ", e);
            return ResponseEntity.internalServerError().body(
                    NotificationResponse.<Page<NotificationDto>>builder()
                            .success(false)
                            .message("Failed to search notifications: " + e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    @GetMapping("/notification-types")
    @Operation(summary = "Get notification types",
            description = "Retrieve all available notification types for filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notification types"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationResponse<List<String>>> getNotificationTypes(Principal principal) {
        try {
            List<String> types = notificationService.getNotificationTypes();

            return ResponseEntity.ok(NotificationResponse.<List<String>>builder()
                    .success(true)
                    .message("Notification types retrieved successfully")
                    .data(types)
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving notification types: ", e);
            return ResponseEntity.internalServerError().body(
                    NotificationResponse.<List<String>>builder()
                            .success(false)
                            .message("Failed to retrieve notification types: " + e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

}