package com.capstone.huddle.notifications.repository;

import com.capstone.huddle.notifications.model.NotificationEntity;
import com.capstone.huddle.users.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID>, JpaSpecificationExecutor<NotificationEntity> {

    Page<NotificationEntity> findByRecipientOrderByCreatedAtDesc(UserEntity recipient, Pageable pageable);

    Long countByRecipientAndIsReadFalse(UserEntity recipient);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.recipient = :recipient AND n.isRead = false")
    void markAllAsReadForUser(UserEntity recipient);

    @Modifying
    void deleteByEntityId(UUID entityId);
}