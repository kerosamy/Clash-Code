package com.clashcode.backend.controller;

import com.clashcode.backend.dto.NotificationDto;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.mapper.NotificationMapper;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@PreAuthorize("hasRole('USER')")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    public NotificationController(
            NotificationService notificationService,
            NotificationMapper notificationMapper
    ) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getUserNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String category
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        List<Notification> notifications;

        if (category != null && !category.isEmpty()) {
            notifications = notificationService.getUserNotificationsByCategory(user.getId(), category);
        } else {
            notifications = notificationService.getUserNotifications(user.getId());
        }

        List<NotificationDto> dtos = notificationMapper.toDtoList(notifications);

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        notificationService.markAsRead(notificationId, user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationDto> getNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Notification notification = notificationService.getNotificationById(notificationId, user.getId());
        NotificationDto dto = notificationMapper.toDto(notification);

        return ResponseEntity.ok(dto);
    }
}