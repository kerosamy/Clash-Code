package com.clashcode.backend.service;

import com.clashcode.backend.dto.NotificationPayload;
import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_send_persistent_notification_savesAndSends() {
        NotificationPayload payload = mock(NotificationPayload.class);
        when(payload.getMode()).thenReturn(NotificationMode.PERSISTENT);
        when(payload.getNotificationType()).thenReturn(NotificationType.MATCH_INVITATION);
        when(payload.getTitle()).thenReturn("Title");
        when(payload.getMessage()).thenReturn("Message");
        when(payload.getDestination("user")).thenReturn("/topic/notifications/user");

        notificationService.send(1L, 2L, "user", payload);

        verify(repository).save(argThat(n ->
                n.getSenderId().equals(1L) &&
                        n.getRecipientId().equals(2L) &&
                        n.getTitle().equals("Title") &&
                        n.getMessage().equals("Message") &&
                        n.getType() == NotificationType.MATCH_INVITATION
        ));

        verify(messagingTemplate).convertAndSend("/topic/notifications/user", payload);
    }

    @Test
    void test_send_nonPersistent_notification_onlySends() {
        NotificationPayload payload = mock(NotificationPayload.class);
        when(payload.getMode()).thenReturn(NotificationMode.EPHEMERAL);
        when(payload.getDestination("user")).thenReturn("/topic/notifications/user");

        notificationService.send(1L, 2L, "user", payload);

        verify(repository, never()).save(any());
        verify(messagingTemplate).convertAndSend("/topic/notifications/user", payload);
    }

    @Test
    void test_getUserNotifications_returnsList() {
        Notification n = Notification.builder()
                .id(1L)
                .senderId(2L)
                .recipientId(1L)
                .type(NotificationType.MATCH_INVITATION)
                .title("Title")
                .message("Message")
                .createdAt(Instant.now())
                .read(false)
                .build();

        when(repository.findByRecipientIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(n));

        List<Notification> result = notificationService.getUserNotifications(1L);

        assertEquals(1, result.size());
        assertEquals(n, result.get(0));
    }

    @Test
    void test_getUnreadCount_returnsCount() {
        when(repository.countByRecipientIdAndReadFalse(1L)).thenReturn(5L);

        long count = notificationService.getUnreadCount(1L);

        assertEquals(5L, count);
    }

    @Test
    void test_markAsRead_marksCorrectNotification() {
        Notification n = Notification.builder()
                .id(1L)
                .recipientId(1L)
                .read(false)
                .type(NotificationType.MATCH_INVITATION)
                .senderId(2L)
                .title("Title")
                .message("Message")
                .createdAt(Instant.now())
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(n));

        notificationService.markAsRead(1L, 1L);

        assertTrue(n.isRead());
        verify(repository).save(n);
    }

    @Test
    void test_markAsRead_doesNotMarkIfDifferentUser() {
        Notification n = Notification.builder()
                .id(1L)
                .recipientId(2L)
                .read(false)
                .type(NotificationType.MATCH_INVITATION)
                .senderId(1L)
                .title("Title")
                .message("Message")
                .createdAt(Instant.now())
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(n));

        notificationService.markAsRead(1L, 1L);

        assertFalse(n.isRead());
        verify(repository, never()).save(any());
    }
}
