package com.clashcode.backend.service;

import com.clashcode.backend.Notification.NotificationPayload;
import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.ArrayList;
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

    @Test
    void test_getUserNotificationsPaginated_returnsPagedResults() {
        // Arrange
        List<Notification> notifications = createNotificationList(3, 1L);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> page = new PageImpl<>(notifications, pageRequest, 3);

        when(repository.findByRecipientId(1L, pageRequest)).thenReturn(page);

        // Act
        Page<Notification> result = notificationService.getUserNotificationsPaginated(1L, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getNumber());
        verify(repository).findByRecipientId(1L, pageRequest);
    }

    @Test
    void test_getUserNotificationsPaginated_returnsEmptyPageWhenNoNotifications() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(repository.findByRecipientId(1L, pageRequest)).thenReturn(emptyPage);

        // Act
        Page<Notification> result = notificationService.getUserNotificationsPaginated(1L, 0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    void test_getUserNotificationsPaginated_handlesMultiplePages() {
        // Arrange
        List<Notification> notifications = createNotificationList(10, 1L);
        PageRequest pageRequest = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> page = new PageImpl<>(notifications, pageRequest, 25);

        when(repository.findByRecipientId(1L, pageRequest)).thenReturn(page);

        // Act
        Page<Notification> result = notificationService.getUserNotificationsPaginated(1L, 1, 10);

        // Assert
        assertEquals(10, result.getContent().size());
        assertEquals(25, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(1, result.getNumber());
    }

    @Test
    void test_getUserNotificationsByCategory_allCategory_excludesSubmission() {
        // Arrange
        List<Notification> notifications = List.of(
                createNotification(1L, 1L, NotificationType.MATCH_INVITATION),
                createNotification(2L, 1L, NotificationType.FRIEND_REQUEST_RECEIVED),
                createNotification(3L, 1L, NotificationType.MATCH_STARTED)
                // SUBMISSION notifications should be excluded
        );
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> page = new PageImpl<>(notifications, pageRequest, notifications.size());

        when(repository.findByRecipientIdExcludingSubmission(1L, "SUBMISSION", pageRequest)).thenReturn(page);

        // Act
        Page<Notification> result = notificationService.getUserNotificationsByCategory(1L, "all", 0, 10);

        // Assert
        assertEquals(notifications.size(), result.getContent().size());
        assertTrue(result.getContent().stream().noneMatch(n -> n.getType().name().contains("SUBMISSION")));
        verify(repository).findByRecipientIdExcludingSubmission(1L, "SUBMISSION", pageRequest);
    }

    @Test
    void test_getUserNotificationsByCategory_matchCategory_returnsOnlyMatchNotifications() {
        // Arrange
        List<Notification> matchNotifications = List.of(
                createNotification(1L, 1L, NotificationType.MATCH_INVITATION),
                createNotification(2L, 1L, NotificationType.MATCH_STARTED)
                // SUBMISSION notifications are NOT included in match category
        );
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> page = new PageImpl<>(matchNotifications, pageRequest, matchNotifications.size());

        when(repository.findByRecipientIdAndTypeContainingKeyword(
                eq(1L), eq("MATCH"), eq(pageRequest)
        )).thenReturn(page);

        // Act
        Page<Notification> result = notificationService.getUserNotificationsByCategory(1L, "match", 0, 10);

        // Assert
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(n -> n.getType().name().contains("MATCH")));
        verify(repository).findByRecipientIdAndTypeContainingKeyword(
                eq(1L), eq("MATCH"), eq(pageRequest)
        );
    }

    @Test
    void test_getUserNotificationsByCategory_friendCategory_returnsFriendNotifications() {
        // Arrange
        List<Notification> friendNotifications = List.of(
                createNotification(1L, 1L, NotificationType.FRIEND_REQUEST_RECEIVED),
                createNotification(2L, 1L, NotificationType.FRIEND_REQUEST_ACCEPTED)
        );
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> page = new PageImpl<>(friendNotifications, pageRequest, friendNotifications.size());

        when(repository.findByRecipientIdAndTypeContainingKeyword(
                eq(1L), eq("FRIEND"), eq(pageRequest)
        )).thenReturn(page);

        // Act
        Page<Notification> result = notificationService.getUserNotificationsByCategory(1L, "friend", 0, 10);

        // Assert
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(n -> n.getType().name().contains("FRIEND")));
        verify(repository).findByRecipientIdAndTypeContainingKeyword(
                eq(1L), eq("FRIEND"), eq(pageRequest)
        );
    }

    @Test
    void test_getUserNotificationsByCategory_caseInsensitive() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> page = new PageImpl<>(List.of(), pageRequest, 0);

        when(repository.findByRecipientIdAndTypeContainingKeyword(
                anyLong(), anyString(), any(PageRequest.class)
        )).thenReturn(page);

        // Act
        notificationService.getUserNotificationsByCategory(1L, "MATCH", 0, 10);
        notificationService.getUserNotificationsByCategory(1L, "Match", 0, 10);
        notificationService.getUserNotificationsByCategory(1L, "match", 0, 10);

        // Assert
        verify(repository, times(3)).findByRecipientIdAndTypeContainingKeyword(
                anyLong(), anyString(), any(PageRequest.class)
        );
    }

    @Test
    void test_getUserNotificationsByCategory_emptyResult() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(repository.findByRecipientIdAndTypeContainingKeyword(
                eq(1L), eq("FRIEND"), eq(pageRequest)
        )).thenReturn(emptyPage);

        // Act
        Page<Notification> result = notificationService.getUserNotificationsByCategory(1L, "friend", 0, 10);

        // Assert
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void test_getUserNotificationsByCategory_pagination_worksCorrectly() {
        // Arrange
        List<Notification> notifications = createNotificationList(5, 1L);
        PageRequest pageRequest = PageRequest.of(2, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> page = new PageImpl<>(notifications, pageRequest, 15);

        when(repository.findByRecipientIdAndTypeContainingKeyword(
                eq(1L), eq("MATCH"), eq(pageRequest)
        )).thenReturn(page);

        // Act
        Page<Notification> result = notificationService.getUserNotificationsByCategory(1L, "match", 2, 5);

        // Assert
        assertEquals(5, result.getContent().size());
        assertEquals(15, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(2, result.getNumber());
        assertEquals(5, result.getSize());
    }

    private List<Notification> createNotificationList(int count, Long recipientId) {
        List<Notification> notifications = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            notifications.add(Notification.builder()
                    .id((long) i + 1)
                    .senderId(2L)
                    .recipientId(recipientId)
                    .type(NotificationType.MATCH_INVITATION)
                    .title("Title " + i)
                    .message("Message " + i)
                    .createdAt(Instant.now().minusSeconds(i))
                    .read(false)
                    .build());
        }
        return notifications;
    }

    private List<Notification> createMixedNotificationList(Long recipientId) {
        return List.of(
                createNotification(1L, recipientId, NotificationType.MATCH_INVITATION),
                createNotification(2L, recipientId, NotificationType.FRIEND_REQUEST_RECEIVED),
                createNotification(3L, recipientId, NotificationType.MATCH_STARTED),
                createNotification(4L, recipientId, NotificationType.SUBMISSION_RECEIVED),
                createNotification(5L, recipientId, NotificationType.FRIEND_REQUEST_ACCEPTED)
        );
    }

    private Notification createNotification(Long id, Long recipientId, NotificationType type) {
        return Notification.builder()
                .id(id)
                .senderId(2L)
                .recipientId(recipientId)
                .type(type)
                .title("Title")
                .message("Message")
                .createdAt(Instant.now())
                .read(false)
                .build();
    }
}