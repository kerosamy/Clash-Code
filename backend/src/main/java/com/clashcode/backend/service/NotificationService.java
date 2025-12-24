package com.clashcode.backend.service;

import com.clashcode.backend.Notification.NotificationPayload;
import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository repository,
                               SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    public void send(
            Long senderId,
            Long recipientId,
            String recipientUsername,
            NotificationPayload payload
    ) {
        if (payload.getMode() == NotificationMode.PERSISTENT) {
            Notification notification = Notification.builder()
                    .senderId(senderId)
                    .recipientId(recipientId)
                    .type(payload.getNotificationType())
                    .title(payload.getTitle())
                    .message(payload.getMessage())
                    .build();
            repository.save(notification);
        }

        messagingTemplate.convertAndSend(
                payload.getDestination(recipientUsername),
                payload
        );
    }

    public Notification getNotificationById(Long notificationId, Long userId) {
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.getRecipientId().equals(userId)) {
            throw new UnauthorizedException("Not your notification");
        }

        return notification;
    }

    public Page<Notification> getUserNotificationsPaginated(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.findByRecipientId(userId, pageRequest);
    }

    public Page<Notification> getUserNotificationsByCategory(Long userId, String category, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Notification> notificationPage;

        if ("match".equalsIgnoreCase(category)) {
            notificationPage = repository.findByRecipientIdAndTypeContainingKeyword(
                    userId, "MATCH", pageRequest
            );
        } else if ("friend".equalsIgnoreCase(category)) {
            notificationPage = repository.findByRecipientIdAndTypeContainingKeyword(
                    userId, "FRIEND", pageRequest
            );
        } else {
            notificationPage = repository.findByRecipientIdExcludingSubmission(
                    userId, "SUBMISSION", pageRequest
            );
        }

        return notificationPage;
    }
    public List<Notification> getUserNotifications(Long userId) {
        return repository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return repository.countByRecipientIdAndReadFalse(userId);
    }

    public void markAsRead(Long notificationId, Long userId) {
        repository.findById(notificationId).ifPresent(n -> {
            if (n.getRecipientId().equals(userId)) {
                n.setRead(true);
                repository.save(n);
            }
        });
    }
}
