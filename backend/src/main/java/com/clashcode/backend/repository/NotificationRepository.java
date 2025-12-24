package com.clashcode.backend.repository;

import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipientIdAndReadFalse(Long recipientId);

    Page<Notification> findByRecipientId(Long recipientId, Pageable pageable);

    // filter by friend or match keyword
    @Query("SELECT n FROM Notification n WHERE n.recipientId = :recipientId " +
            "AND CAST(n.type AS string) LIKE %:keyword% " +
            "ORDER BY n.createdAt DESC")
    Page<Notification> findByRecipientIdAndTypeContainingKeyword(
            @Param("recipientId") Long recipientId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Optional<Notification> findTopBySenderIdAndRecipientIdAndTypeOrderByCreatedAtDesc(
            Long senderId,
            Long recipientId,
            NotificationType type
    );

}