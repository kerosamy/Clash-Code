package com.clashcode.backend.service;

import com.clashcode.backend.dto.MatchNotificationDto;
import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.model.Match;
import com.clashcode.backend.model.MatchParticipant;
import com.clashcode.backend.model.User;
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
    void send_persistsAndBroadcastsNotification() {
        Notification notification = Notification.builder()
                .id(1L)
                .recipientId(1L)
                .senderId(2L)
                .type(NotificationType.FRIEND_REQUEST_RECEIVED)
                .title("Friend Request")
                .message("You have a new friend request")
                .createdAt(Instant.now())
                .read(false)
                .build();

        when(repository.save(any(Notification.class))).thenReturn(notification);

        notificationService.send(2L, 1L, NotificationType.FRIEND_REQUEST_RECEIVED,
                "Friend Request", "You have a new friend request");

        verify(repository).save(any(Notification.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/notifications/1"), any(Notification.class));
    }

    @Test
    void sendMatchPop_broadcastsToAllParticipants() {
        User u1 = User.builder().id(1L).username("Alice").build();
        User u2 = User.builder().id(2L).username("Bob").build();

        MatchParticipant p1 = MatchParticipant.builder().user(u1).build();
        MatchParticipant p2 = MatchParticipant.builder().user(u2).build();

        Match match = Match.builder().id(99L).participants(List.of(p1, p2)).build();

        notificationService.sendMatchPop(match, "Code Submission", "Alice submitted code", "Alice");

        verify(messagingTemplate).convertAndSend(eq("/topic/match-pop/1"), any(MatchNotificationDto.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/match-pop/2"), any(MatchNotificationDto.class));
    }

    @Test
    void getUserNotifications_returnsList() {
        Notification n = Notification.builder().id(1L).recipientId(1L).title("Test").message("Msg").createdAt(Instant.now()).build();
        when(repository.findByRecipientIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(n));

        List<Notification> result = notificationService.getUserNotifications(1L);

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getTitle());
    }

    @Test
    void getUnreadCount_returnsCount() {
        when(repository.countByRecipientIdAndReadFalse(1L)).thenReturn(3L);

        long count = notificationService.getUnreadCount(1L);

        assertEquals(3L, count);
    }

    @Test
    void markAsRead_updatesNotification() {
        Notification n = Notification.builder().id(1L).recipientId(1L).read(false).build();
        when(repository.findById(1L)).thenReturn(Optional.of(n));

        notificationService.markAsRead(1L, 1L);

        assertTrue(n.isRead());
        verify(repository).save(n);
    }

    @Test
    void markAsRead_doesNothingIfNotRecipient() {
        Notification n = Notification.builder().id(1L).recipientId(2L).read(false).build();
        when(repository.findById(1L)).thenReturn(Optional.of(n));

        notificationService.markAsRead(1L, 1L);

        assertFalse(n.isRead());
        verify(repository, never()).save(n);
    }
}
