package com.clashcode.backend.service;

import com.clashcode.backend.Notification.NotificationPayload;
import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.repository.NotificationRepository;
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

    public List<Notification> getUserNotificationsByCategory(Long userId, String category) {
        List<Notification> allNotifications = getUserNotifications(userId);

        if ("match".equalsIgnoreCase(category)) {

            return allNotifications.stream()
                    .filter(n -> {
                        String type = n.getType().name();
                        return type.contains("MATCH") || type.contains("SUBMISSION") || type.contains("OPPONENT");
                    })
                    .collect(Collectors.toList());
        } else if ("friend".equalsIgnoreCase(category)) {

            return allNotifications.stream()
                    .filter(n -> {
                        String type = n.getType().name();
                        return type.contains("FRIEND");
                    })
                    .collect(Collectors.toList());
        }

        return allNotifications;
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
