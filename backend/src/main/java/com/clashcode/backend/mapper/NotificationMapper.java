package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.NotificationDto;
import com.clashcode.backend.model.Notification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationMapper {

    // Single notification - sender username passed directly
    public NotificationDto toDto(Notification notification, String senderUsername) {
        return NotificationDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .senderId(notification.getSenderId())
                .senderUsername(senderUsername != null ? senderUsername : "Unknown")
                .recipientId(notification.getRecipientId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .read(notification.isRead())
                .build();
    }

    // For lists - if you need it, but typically you'll map Page elements individually
    public List<NotificationDto> toDtoList(List<Notification> notifications, String senderUsername) {
        if (notifications.isEmpty()) {
            return List.of();
        }

        return notifications.stream()
                .map(notification -> toDto(notification, senderUsername))
                .toList();
    }
}