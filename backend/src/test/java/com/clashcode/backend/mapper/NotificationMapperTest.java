package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.NotificationDto;
import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationMapperTest {

    private NotificationMapper notificationMapper;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        notificationMapper = new NotificationMapper(userRepository);
    }

    @Test
    void test_toDto_withValidSender() {
        User sender = User.builder()
                .id(1L)
                .username("SenderUser")
                .build();

        Notification notification = Notification.builder()
                .id(100L)
                .type(NotificationType.MATCH_INVITATION)
                .senderId(1L)
                .recipientId(2L)
                .title("Match Invite")
                .message("You've been invited")
                .createdAt(Instant.now())
                .read(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));

        NotificationDto dto = notificationMapper.toDto(notification);

        assertEquals(100L, dto.getId());
        assertEquals(NotificationType.MATCH_INVITATION, dto.getType());
        assertEquals(1L, dto.getSenderId());
        assertEquals("SenderUser", dto.getSenderUsername());
        assertEquals(2L, dto.getRecipientId());
        assertEquals("Match Invite", dto.getTitle());
        assertEquals("You've been invited", dto.getMessage());
        assertFalse(dto.isRead());
        assertNotNull(dto.getCreatedAt());
    }

    @Test
    void test_toDto_withNonExistentSender() {
        Notification notification = Notification.builder()
                .id(200L)
                .type(NotificationType.MATCH_STARTED)
                .senderId(999L)
                .recipientId(2L)
                .title("Match Started")
                .message("The match has started")
                .createdAt(Instant.now())
                .read(true)
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotificationDto dto = notificationMapper.toDto(notification);

        assertEquals(200L, dto.getId());
        assertEquals("Unknown", dto.getSenderUsername());
        assertTrue(dto.isRead());
    }

    @Test
    void test_toDto_withNullSenderId() {
        Notification notification = Notification.builder()
                .id(300L)
                .type(NotificationType.MATCH_STATUS)
                .senderId(null)
                .recipientId(2L)
                .title("System Notification")
                .message("System message")
                .createdAt(Instant.now())
                .read(false)
                .build();

        NotificationDto dto = notificationMapper.toDto(notification);

        assertEquals(300L, dto.getId());
        assertNull(dto.getSenderId());
        assertEquals("Unknown", dto.getSenderUsername());
        assertFalse(dto.isRead());
    }

    @Test
    void test_toDtoList_withEmptyList() {
        List<NotificationDto> result = notificationMapper.toDtoList(Collections.emptyList());

        assertTrue(result.isEmpty());
        verify(userRepository, never()).findAllById(anySet());
    }

    @Test
    void test_toDtoList_withMultipleNotifications() {
        User sender1 = User.builder().id(1L).username("Alice").build();
        User sender2 = User.builder().id(2L).username("Bob").build();

        Notification notification1 = Notification.builder()
                .id(1L)
                .type(NotificationType.MATCH_INVITATION)
                .senderId(1L)
                .recipientId(3L)
                .title("Invite 1")
                .message("Message 1")
                .createdAt(Instant.now())
                .read(false)
                .build();

        Notification notification2 = Notification.builder()
                .id(2L)
                .type(NotificationType.MATCH_STARTED)
                .senderId(2L)
                .recipientId(3L)
                .title("Invite 2")
                .message("Message 2")
                .createdAt(Instant.now())
                .read(true)
                .build();

        doReturn(Arrays.asList(sender1, sender2))
                .when(userRepository).findAllById(any());

        List<NotificationDto> result = notificationMapper.toDtoList(Arrays.asList(notification1, notification2));

        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getSenderUsername());
        assertEquals("Bob", result.get(1).getSenderUsername());
        assertFalse(result.get(0).isRead());
        assertTrue(result.get(1).isRead());
    }


    @Test
    void test_toDtoList_withMissingSenders() {
        User sender1 = User.builder().id(1L).username("Alice").build();

        Notification notification1 = Notification.builder()
                .id(1L)
                .type(NotificationType.FRIEND_REQUEST_RECEIVED)
                .senderId(1L)
                .recipientId(3L)
                .title("Title 1")
                .message("Message 1")
                .createdAt(Instant.now())
                .read(false)
                .build();

        Notification notification2 = Notification.builder()
                .id(2L)
                .type(NotificationType.FRIEND_REQUEST_RECEIVED)
                .senderId(999L)
                .recipientId(3L)
                .title("Title 2")
                .message("Message 2")
                .createdAt(Instant.now())
                .read(false)
                .build();

        doReturn(Collections.singletonList(sender1))
                .when(userRepository).findAllById(any());

        List<NotificationDto> result = notificationMapper.toDtoList(Arrays.asList(notification1, notification2));

        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getSenderUsername());
        assertEquals("Unknown", result.get(1).getSenderUsername());
    }


    @Test
    void test_toDtoList_optimizesBatchFetch() {
        User sender = User.builder().id(1L).username("BatchUser").build();

        Notification n1 = Notification.builder()
                .id(1L)
                .type(NotificationType.MATCH_INVITATION)
                .senderId(1L)
                .recipientId(2L)
                .title("T1")
                .message("M1")
                .createdAt(Instant.now())
                .read(false)
                .build();

        Notification n2 = Notification.builder()
                .id(2L)
                .type(NotificationType.MATCH_INVITATION)
                .senderId(1L)
                .recipientId(2L)
                .title("T2")
                .message("M2")
                .createdAt(Instant.now())
                .read(false)
                .build();

        when(userRepository.findAllById(Collections.singleton(1L)))
                .thenReturn(Collections.singletonList(sender));

        List<NotificationDto> result = notificationMapper.toDtoList(Arrays.asList(n1, n2));

        assertEquals(2, result.size());
        assertEquals("BatchUser", result.get(0).getSenderUsername());
        assertEquals("BatchUser", result.get(1).getSenderUsername());
        verify(userRepository, times(1)).findAllById(anySet());
    }

    @Test
    void test_toDtoList_withNullSenderIds() {
        Notification systemNotification = Notification.builder()
                .id(1L)
                .type(NotificationType.MATCH_STATUS)
                .senderId(null)
                .recipientId(2L)
                .title("System Message")
                .message("System notification")
                .createdAt(Instant.now())
                .read(false)
                .build();

        User sender = User.builder().id(3L).username("Alice").build();

        Notification userNotification = Notification.builder()
                .id(2L)
                .type(NotificationType.FRIEND_REQUEST_RECEIVED)
                .senderId(3L)
                .recipientId(2L)
                .title("Friend Request")
                .message("User notification")
                .createdAt(Instant.now())
                .read(false)
                .build();

        doReturn(Collections.singletonList(sender))
                .when(userRepository).findAllById(any());

        List<NotificationDto> result = notificationMapper.toDtoList(
                Arrays.asList(systemNotification, userNotification)
        );

        assertEquals(2, result.size());
        assertEquals("Unknown", result.get(0).getSenderUsername());
        assertEquals("Alice", result.get(1).getSenderUsername());
    }

    @Test
    void test_toDto_withDifferentNotificationTypes() {
        User sender = User.builder().id(1L).username("TestUser").build();

        // Test FRIEND_REQUEST_RECEIVED
        Notification friendRequest = Notification.builder()
                .id(1L)
                .type(NotificationType.FRIEND_REQUEST_RECEIVED)
                .senderId(1L)
                .recipientId(2L)
                .title("Friend Request")
                .message("wants to be your friend")
                .createdAt(Instant.now())
                .read(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));

        NotificationDto dto1 = notificationMapper.toDto(friendRequest);
        assertEquals(NotificationType.FRIEND_REQUEST_RECEIVED, dto1.getType());
        assertEquals("TestUser", dto1.getSenderUsername());

        // Test MATCH_COMPLETED
        Notification matchResult = Notification.builder()
                .id(2L)
                .type(NotificationType.MATCH_COMPLETED)
                .senderId(1L)
                .recipientId(2L)
                .title("Match Result")
                .message("Match completed")
                .createdAt(Instant.now())
                .read(false)
                .build();

        NotificationDto dto2 = notificationMapper.toDto(matchResult);
        assertEquals(NotificationType.MATCH_COMPLETED, dto2.getType());
    }

    @Test
    void test_toDtoList_withSingleNotification() {
        User sender = User.builder().id(1L).username("SingleUser").build();

        Notification notification = Notification.builder()
                .id(1L)
                .type(NotificationType.MATCH_INVITATION)
                .senderId(1L)
                .recipientId(2L)
                .title("Single Notification")
                .message("Test message")
                .createdAt(Instant.now())
                .read(false)
                .build();

        when(userRepository.findAllById(Collections.singleton(1L)))
                .thenReturn(Collections.singletonList(sender));

        List<NotificationDto> result = notificationMapper.toDtoList(Collections.singletonList(notification));

        assertEquals(1, result.size());
        assertEquals("SingleUser", result.getFirst().getSenderUsername());
        assertEquals("Single Notification", result.getFirst().getTitle());
    }

    @Test
    void test_toDto_preservesAllFields() {
        User sender = User.builder().id(5L).username("CompleteUser").build();
        Instant now = Instant.now();

        Notification notification = Notification.builder()
                .id(999L)
                .type(NotificationType.FRIEND_REQUEST_ACCEPTED)
                .senderId(5L)
                .recipientId(10L)
                .title("Complete Title")
                .message("Complete Message")
                .createdAt(now)
                .read(true)
                .build();

        when(userRepository.findById(5L)).thenReturn(Optional.of(sender));

        NotificationDto dto = notificationMapper.toDto(notification);

        assertEquals(999L, dto.getId());
        assertEquals(NotificationType.FRIEND_REQUEST_ACCEPTED, dto.getType());
        assertEquals(5L, dto.getSenderId());
        assertEquals("CompleteUser", dto.getSenderUsername());
        assertEquals(10L, dto.getRecipientId());
        assertEquals("Complete Title", dto.getTitle());
        assertEquals("Complete Message", dto.getMessage());
        assertEquals(now, dto.getCreatedAt());
        assertTrue(dto.isRead());
    }
}
