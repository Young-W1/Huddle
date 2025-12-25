package com.capstone.huddle.notifications.service;

import com.capstone.huddle.notifications.dto.NotificationDto;
import com.capstone.huddle.notifications.dto.NotificationFilterDto;
import com.capstone.huddle.notifications.model.NotificationEntity;
import com.capstone.huddle.notifications.model.NotificationEntity.NotificationType;
import com.capstone.huddle.notifications.repository.NotificationRepository;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.Notification;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createFollowNotification(UserEntity follower, UserEntity following) {
        String message = String.format("%s started following you", follower.getUsername());
        createNotification(following, follower, NotificationType.NEW_FOLLOWER, message, follower.getId());
    }

    @Transactional
    public void createCommentVoteNotification(UserEntity voter, UserEntity commentOwner,
                                              UUID commentId, boolean isUpvote) {
        NotificationType type = isUpvote ? NotificationType.COMMENT_UPVOTE : NotificationType.COMMENT_DOWNVOTE;
        String action = isUpvote ? "upvoted" : "downvoted";
        String message = String.format("%s %s your comment", voter.getUsername(), action);
        createNotification(commentOwner, voter, type, message, commentId);
    }

    @Transactional
    public void createNewCommentNotification(UserEntity commenter, UserEntity articleAuthor,
                                             UUID articleId, String articleTitle) {
        String message = String.format("%s commented on your article \"%s\"",
                commenter.getUsername(), articleTitle);
        createNotification(articleAuthor, commenter, NotificationType.NEW_COMMENT, message, articleId);
    }

    private void createNotification(UserEntity recipient, UserEntity actor,
                                    NotificationType type, String message, UUID entityId) {
        // Validate inputs
        if (recipient == null || actor == null) {
            log.warn("Cannot create notification: recipient or actor is null");
            return;
        }

        // Don't notify if actor and recipient are the same
        if (recipient.getId().equals(actor.getId())) {
            return;
        }

        try {
            NotificationEntity notification = NotificationEntity.builder()
                    .recipient(recipient)
                    .actor(actor)
                    .type(type)
                    .message(message)
                    .entityId(entityId)
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);
            log.info("Notification created for user {} of type {}", recipient.getUsername(), type);
        } catch (Exception e) {
            log.error("Failed to create notification for user {}: {}", recipient.getUsername(), e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(String username, Pageable pageable) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user, pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }

    @Transactional
    public void markAllAsRead(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        notificationRepository.markAllAsReadForUser(user);
        log.info("All notifications marked as read for user {}", username);
    }

    @Transactional
    public void markAsRead(String username, UUID notificationId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException("Cannot mark notification not belonging to user");
        }

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
            log.info("Notification {} marked as read for user {}", notificationId, username);
        }
    }

    private NotificationDto mapToDto(NotificationEntity notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .actorUsername(notification.getActor().getUsername())
                .actorProfilePicture(notification.getActor().getProfile() != null
                        ? notification.getActor().getProfile().getProfilePicture() : null)
                .type(notification.getType().toString())
                .message(notification.getMessage())
                .entityId(notification.getEntityId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public Page<NotificationDto> searchNotifications(String username, NotificationFilterDto filter, Pageable pageable) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Specification<NotificationEntity> spec = Specification.where(belongsToUser(user));

        if (filter.getSearchTerm() != null && !filter.getSearchTerm().trim().isEmpty()) {
            spec = spec.and(containsSearchTerm(filter.getSearchTerm()));
        }

        if (filter.getNotificationType() != null) {
            spec = spec.and(hasNotificationType(filter.getNotificationType()));
        }

        if (filter.getIsRead() != null) {
            spec = spec.and(hasReadStatus(filter.getIsRead()));
        }

        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            spec = spec.and(withinDateRange(filter.getStartDate(), filter.getEndDate()));
        }

        Page<NotificationEntity> notifications = notificationRepository.findAll(spec, pageable);
        return notifications.map(this::mapToDto);
    }

    // Specification methods for filtering
    private Specification<NotificationEntity> belongsToUser(UserEntity user) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("recipient"), user);
    }

    private Specification<NotificationEntity> containsSearchTerm(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("message")), likePattern);
        };
    }

    private Specification<NotificationEntity> hasNotificationType(String notificationType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), NotificationType.valueOf(notificationType));
    }

    private Specification<NotificationEntity> hasReadStatus(boolean isRead) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isRead"), isRead);
    }

    private Specification<NotificationEntity> withinDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
    }

    public List<String> getNotificationTypes() {
        return Arrays.stream(NotificationType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createReplyNotification(UserEntity replier, UserEntity parentCommentAuthor,
                                        UUID articleId, String articleTitle) {
        String message = String.format("%s replied to your comment on \"%s\"",
                replier.getUsername(), articleTitle);
        createNotification(parentCommentAuthor, replier, NotificationType.COMMENT_REPLY, message, articleId);
    }


}