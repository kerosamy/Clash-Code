package com.clashcode.backend.Dto;

import com.clashcode.backend.dto.NotificationDto;
import com.clashcode.backend.enums.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationDtoTest {

    @Test
    @DisplayName("Should create NotificationDto using builder")
    void shouldCreateDtoUsingBuilder() {
        Instant now = Instant.now();

        NotificationDto dto = NotificationDto.builder()
                .id(1L)
                .type(NotificationType.MATCH_INVITATION)
                .senderId(10L)
                .senderUsername("john_doe")
                .recipientId(20L)
                .title("Match Invitation")
                .message("You have been invited to a match")
                .createdAt(now)
                .read(false)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getType()).isEqualTo(NotificationType.MATCH_INVITATION);
        assertThat(dto.getSenderId()).isEqualTo(10L);
        assertThat(dto.getSenderUsername()).isEqualTo("john_doe");
        assertThat(dto.getRecipientId()).isEqualTo(20L);
        assertThat(dto.getTitle()).isEqualTo("Match Invitation");
        assertThat(dto.getMessage()).isEqualTo("You have been invited to a match");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.isRead()).isFalse();
    }

    @Test
    @DisplayName("Should handle read notification")
    void shouldHandleReadNotification() {
        NotificationDto dto = NotificationDto.builder()
                .read(true)
                .build();

        assertThat(dto.isRead()).isTrue();
    }

    @Test
    @DisplayName("Should support setter methods")
    void shouldSupportSetterMethods() {
        NotificationDto dto = new NotificationDto();
        Instant now = Instant.now();

        dto.setId(5L);
        dto.setType(NotificationType.FRIEND_REQUEST_RECEIVED);
        dto.setSenderId(100L);
        dto.setSenderUsername("alice");
        dto.setRecipientId(200L);
        dto.setTitle("Friend Request");
        dto.setMessage("Alice wants to be your friend");
        dto.setCreatedAt(now);
        dto.setRead(false);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getType()).isEqualTo(NotificationType.FRIEND_REQUEST_RECEIVED);
        assertThat(dto.getSenderId()).isEqualTo(100L);
        assertThat(dto.getSenderUsername()).isEqualTo("alice");
        assertThat(dto.getRecipientId()).isEqualTo(200L);
        assertThat(dto.getTitle()).isEqualTo("Friend Request");
        assertThat(dto.getMessage()).isEqualTo("Alice wants to be your friend");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.isRead()).isFalse();
    }

    @Test
    @DisplayName("Should create using no-args constructor")
    void shouldCreateUsingNoArgsConstructor() {
        NotificationDto dto = new NotificationDto();

        assertThat(dto).isNotNull();
        assertThat(dto.isRead()).isFalse();
    }

    @Test
    @DisplayName("Should create using all-args constructor")
    void shouldCreateUsingAllArgsConstructor() {
        Instant now = Instant.now();

        NotificationDto dto = new NotificationDto(
                1L, NotificationType.MATCH_INVITATION, 50L, "admin",
                100L, "System Alert", "Maintenance scheduled", now, true
        );

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getType()).isEqualTo(NotificationType.MATCH_INVITATION);
        assertThat(dto.getSenderId()).isEqualTo(50L);
        assertThat(dto.getSenderUsername()).isEqualTo("admin");
        assertThat(dto.getRecipientId()).isEqualTo(100L);
        assertThat(dto.getTitle()).isEqualTo("System Alert");
        assertThat(dto.getMessage()).isEqualTo("Maintenance scheduled");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.isRead()).isTrue();
    }

    @Test
    @DisplayName("Should handle null timestamp")
    void shouldHandleNullTimestamp() {
        NotificationDto dto = NotificationDto.builder()
                .createdAt(null)
                .build();

        assertThat(dto.getCreatedAt()).isNull();
    }

    @Nested
    @DisplayName("Notification read status tests")
    class ReadStatusTests {

        @Test
        @DisplayName("Should mark notification as read")
        void shouldMarkAsRead() {
            NotificationDto dto = NotificationDto.builder()
                    .read(false)
                    .build();

            dto.setRead(true);

            assertThat(dto.isRead()).isTrue();
        }

        @Test
        @DisplayName("Should mark notification as unread")
        void shouldMarkAsUnread() {
            NotificationDto dto = NotificationDto.builder()
                    .read(true)
                    .build();

            dto.setRead(false);

            assertThat(dto.isRead()).isFalse();
        }
    }
}