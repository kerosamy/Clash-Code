package com.clashcode.backend.service;

import com.clashcode.backend.dto.NotificationPayload;
import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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
                    .read(false)
                    .build();
            repository.save(notification);
        }

        messagingTemplate.convertAndSend(
                payload.getDestination(recipientUsername),
                payload
        );
        System.out.println("📤 WS SEND TO: " + payload.getDestination(recipientUsername));
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
