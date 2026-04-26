package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.NotificationDto;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {

    private final UserRepository userRepository;

    public NotificationMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public NotificationDto toDto(Notification notification) {
        User sender = userRepository.findById(notification.getSenderId())
                .orElse(null);

        return NotificationDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .senderId(notification.getSenderId())
                .senderUsername(sender != null ? sender.getUsername() : "Unknown")
                .recipientId(notification.getRecipientId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .read(notification.isRead())
                .build();
    }

    public List<NotificationDto> toDtoList(List<Notification> notifications) {
        if (notifications.isEmpty()) {
            return List.of();
        }

        // Optimize: Fetch all unique sender IDs in one query
        Set<Long> senderIds = notifications.stream()
                .map(Notification::getSenderId)
                .collect(Collectors.toSet());

        Map<Long, String> senderUsernameMap = userRepository.findAllById(senderIds)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        return notifications.stream()
                .map(notification -> NotificationDto.builder()
                        .id(notification.getId())
                        .type(notification.getType())
                        .senderId(notification.getSenderId())
                        .senderUsername(senderUsernameMap.getOrDefault(
                                notification.getSenderId(),
                                "Unknown"
                        ))
                        .recipientId(notification.getRecipientId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .createdAt(notification.getCreatedAt())
                        .read(notification.isRead())
                        .build())
                .toList();
    }
}