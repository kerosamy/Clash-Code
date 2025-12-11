package com.clashcode.backend.service;

import com.clashcode.backend.dto.MatchNotificationDto;
import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.model.Match;
import com.clashcode.backend.model.MatchParticipant;
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

    public void send(Long recipientId, NotificationType type, String title, String message) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .type(type)
                .title(title)
                .message(message)
                .read(false)
                .build();

        Notification saved = repository.save(notification);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + recipientId,
                saved
        );
    }

    public void sendMatchPop(Match match, String title, String message, String senderUsername) {
        for (MatchParticipant participant : match.getParticipants()) {
            Long recipientId = participant.getUser().getId();

            MatchNotificationDto notification = new MatchNotificationDto(
                    match.getId(),
                    senderUsername,
                    title,
                    message
            );
            messagingTemplate.convertAndSend(
                    "/topic/match-pop/" + recipientId,
                    notification
            );
        }
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
