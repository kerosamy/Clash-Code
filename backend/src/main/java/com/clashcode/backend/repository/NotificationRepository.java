package com.clashcode.backend.repository;

import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipientIdAndReadFalse(Long recipientId);

    Optional<Notification> findTopBySenderIdAndRecipientIdAndTypeOrderByCreatedAtDesc(
            Long senderId,
            Long recipientId,
            NotificationType type
    );
}