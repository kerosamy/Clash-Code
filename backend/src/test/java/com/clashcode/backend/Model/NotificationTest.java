package com.clashcode.backend.Model;

import com.clashcode.backend.enums.*;
import com.clashcode.backend.model.Notification;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void testNotificationGettersAndSetters() {
        Notification notification = new Notification();
        Instant now = Instant.now();

        notification.setId(1L);
        notification.setType(NotificationType.FRIEND_REQUEST_RECEIVED);
        notification.setSenderId(10L);
        notification.setRecipientId(20L);
        notification.setTitle("New Friend Request");
        notification.setMessage("User X wants to be your friend");
        notification.setCreatedAt(now);
        notification.setRead(true);

        assertEquals(1L, notification.getId());
        assertEquals(NotificationType.FRIEND_REQUEST_RECEIVED, notification.getType());
        assertEquals(10L, notification.getSenderId());
        assertEquals(20L, notification.getRecipientId());
        assertEquals("New Friend Request", notification.getTitle());
        assertEquals("User X wants to be your friend", notification.getMessage());
        assertEquals(now, notification.getCreatedAt());
        assertTrue(notification.isRead());
    }

    @Test
    void testNotificationBuilder() {
        Instant now = Instant.now();

        Notification notification = Notification.builder()
                .id(1L)
                .type(NotificationType.MATCH_INVITATION)
                .senderId(10L)
                .recipientId(20L)
                .title("Match Invitation")
                .message("Join the match")
                .createdAt(now)
                .read(false)
                .build();

        assertEquals(1L, notification.getId());
        assertEquals(NotificationType.MATCH_INVITATION, notification.getType());
        assertEquals(10L, notification.getSenderId());
        assertEquals(20L, notification.getRecipientId());
        assertEquals("Match Invitation", notification.getTitle());
        assertEquals("Join the match", notification.getMessage());
        assertEquals(now, notification.getCreatedAt());
        assertFalse(notification.isRead());
    }

    @Test
    void testNotificationBuilderDefault() {
        Notification notification = Notification.builder()
                .type(NotificationType.MATCH_COMPLETED)
                .senderId(1L)
                .recipientId(2L)
                .title("System")
                .message("System message")
                .build();

        assertFalse(notification.isRead());
    }

    @Test
    void testNotificationPrePersist() {
        Notification notification = new Notification();
        notification.onCreate();

        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void testNotificationPrePersistDoesNotOverwrite() {
        Instant customTime = Instant.parse("2023-01-01T00:00:00Z");
        Notification notification = new Notification();
        notification.setCreatedAt(customTime);
        notification.onCreate();

        assertEquals(customTime, notification.getCreatedAt());
    }

    @Test
    void testNotificationNoArgsConstructor() {
        Notification notification = new Notification();
        assertNotNull(notification);
    }

    @Test
    void testNotificationAllArgsConstructor() {
        Instant now = Instant.now();

        Notification notification = new Notification(1L, NotificationType.FRIEND_REQUEST_RECEIVED, 10L, 20L, "Title", "Message", now, true);

        assertEquals(1L, notification.getId());
        assertEquals(NotificationType.FRIEND_REQUEST_RECEIVED, notification.getType());
        assertEquals(10L, notification.getSenderId());
        assertEquals(20L, notification.getRecipientId());
        assertEquals("Title", notification.getTitle());
        assertEquals("Message", notification.getMessage());
        assertEquals(now, notification.getCreatedAt());
        assertTrue(notification.isRead());
    }
}
