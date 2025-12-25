package com.capstone.huddle.notifications.service;

import com.capstone.huddle.notifications.dto.NotificationDto;
import com.capstone.huddle.notifications.model.NotificationEntity;
import com.capstone.huddle.notifications.model.NotificationEntity.NotificationType;
import com.capstone.huddle.notifications.repository.NotificationRepository;
import com.capstone.huddle.users.model.ProfileEntity;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private UserEntity testUser;
    private UserEntity actorUser;
    private ProfileEntity actorProfile;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .build();

        actorProfile = ProfileEntity.builder()
                .profilePicture("profile-pic-url")
                .build();

        actorUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("actor")
                .profile(actorProfile)
                .build();
    }

    @Test
    void createFollowNotification_shouldCreateNotification() {
        // Act
        notificationService.createFollowNotification(actorUser, testUser);

        // Assert
        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository).save(captor.capture());

        NotificationEntity saved = captor.getValue();
        assertThat(saved.getRecipient()).isEqualTo(testUser);
        assertThat(saved.getActor()).isEqualTo(actorUser);
        assertThat(saved.getType()).isEqualTo(NotificationType.NEW_FOLLOWER);
        assertThat(saved.getMessage()).isEqualTo("actor started following you");
        assertThat(saved.getEntityId()).isEqualTo(actorUser.getId());
        assertThat(saved.getIsRead()).isFalse();
    }

    @Test
    void createFollowNotification_shouldNotCreateWhenSelfFollow() {
        // Act
        notificationService.createFollowNotification(testUser, testUser);

        // Assert
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void createCommentVoteNotification_shouldCreateUpvoteNotification() {
        UUID commentId = UUID.randomUUID();

        // Act
        notificationService.createCommentVoteNotification(actorUser, testUser, commentId, true);

        // Assert
        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository).save(captor.capture());

        NotificationEntity saved = captor.getValue();
        assertThat(saved.getType()).isEqualTo(NotificationType.COMMENT_UPVOTE);
        assertThat(saved.getMessage()).isEqualTo("actor upvoted your comment");
        assertThat(saved.getEntityId()).isEqualTo(commentId);
    }

    @Test
    void createCommentVoteNotification_shouldCreateDownvoteNotification() {
        UUID commentId = UUID.randomUUID();

        // Act
        notificationService.createCommentVoteNotification(actorUser, testUser, commentId, false);

        // Assert
        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository).save(captor.capture());

        NotificationEntity saved = captor.getValue();
        assertThat(saved.getType()).isEqualTo(NotificationType.COMMENT_DOWNVOTE);
        assertThat(saved.getMessage()).isEqualTo("actor downvoted your comment");
    }

    @Test
    void createNewCommentNotification_shouldCreateNotification() {
        UUID articleId = UUID.randomUUID();
        String articleTitle = "Test Article";

        // Act
        notificationService.createNewCommentNotification(actorUser, testUser, articleId, articleTitle);

        // Assert
        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository).save(captor.capture());

        NotificationEntity saved = captor.getValue();
        assertThat(saved.getType()).isEqualTo(NotificationType.NEW_COMMENT);
        assertThat(saved.getMessage()).isEqualTo("actor commented on your article \"Test Article\"");
        assertThat(saved.getEntityId()).isEqualTo(articleId);
    }

    @Test
    void getUserNotifications_shouldReturnPagedNotifications() {
        // Arrange
        NotificationEntity notification = NotificationEntity.builder()
                .id(UUID.randomUUID())
                .recipient(testUser)
                .actor(actorUser)
                .type(NotificationType.NEW_FOLLOWER)
                .message("Test notification")
                .entityId(UUID.randomUUID())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<NotificationEntity> notificationPage = new PageImpl<>(List.of(notification));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(notificationRepository.findByRecipientOrderByCreatedAtDesc(testUser, pageable))
                .thenReturn(notificationPage);

        // Act
        Page<NotificationDto> result = notificationService.getUserNotifications("testuser", pageable);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        NotificationDto dto = result.getContent().get(0);
        assertThat(dto.getActorUsername()).isEqualTo("actor");
        assertThat(dto.getActorProfilePicture()).isEqualTo("profile-pic-url");
        assertThat(dto.getType()).isEqualTo("NEW_FOLLOWER");
        assertThat(dto.getMessage()).isEqualTo("Test notification");
        assertThat(dto.getIsRead()).isFalse();
    }

    @Test
    void getUserNotifications_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> notificationService.getUserNotifications("unknown", PageRequest.of(0, 10)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void getUnreadCount_shouldReturnCount() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(notificationRepository.countByRecipientAndIsReadFalse(testUser)).thenReturn(5L);

        // Act
        Long count = notificationService.getUnreadCount("testuser");

        // Assert
        assertThat(count).isEqualTo(5L);
    }

    @Test
    void markAllAsRead_shouldCallRepository() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        notificationService.markAllAsRead("testuser");

        // Assert
        verify(notificationRepository).markAllAsReadForUser(testUser);
    }

    @Test
    void mapToDto_shouldHandleNullProfile() {
        // Arrange
        UserEntity userWithoutProfile = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("noProfileUser")
                .profile(null)
                .build();

        NotificationEntity notification = NotificationEntity.builder()
                .id(UUID.randomUUID())
                .actor(userWithoutProfile)
                .type(NotificationType.NEW_FOLLOWER)
                .message("Test")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(notificationRepository.findByRecipientOrderByCreatedAtDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(notification)));

        // Act
        Page<NotificationDto> result = notificationService.getUserNotifications("testuser", PageRequest.of(0, 10));

        // Assert
        assertThat(result.getContent().get(0).getActorProfilePicture()).isNull();
    }
}