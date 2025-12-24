package com.clashcode.backend.controller;

import com.clashcode.backend.dto.NotificationDto;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.mapper.NotificationMapper;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.NotificationService;
import org.springframework.data.domain.Page;
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

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    @GetMapping
    public ResponseEntity<Page<NotificationDto>> getUserNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Page<Notification> notificationPage;

        if (category != null && !category.isEmpty()) {
            notificationPage = notificationService.getUserNotificationsByCategory(
                    user.getId(), category, page, size
            );
        } else {
            notificationPage = notificationService.getUserNotificationsPaginated(
                    user.getId(), page, size
            );
        }

        Page<NotificationDto> dtoPage = notificationPage.map(
                notification -> notificationMapper.toDto(notification, user.getUsername())
        );

        return ResponseEntity.ok(dtoPage);
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
        NotificationDto dto = notificationMapper.toDto(notification, user.getUsername());

        return ResponseEntity.ok(dto);
    }
}