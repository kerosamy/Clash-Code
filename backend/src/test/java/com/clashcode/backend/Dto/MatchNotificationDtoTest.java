package com.clashcode.backend.Dto;

import com.clashcode.backend.Notification.Dtos.MatchNotificationDto;
import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchNotificationDtoTest {

    @Test
    void test_allArgsConstructor_SetsFieldsCorrectly() {
        MatchNotificationDto dto = new MatchNotificationDto(
                123L,
                "alice",
                NotificationType.MATCH_INVITATION,
                "Match Started",
                "You have a new match",
                "SUCCESS",
                10,
                20,
                NotificationMode.EPHEMERAL
        );

        assertEquals(123L, dto.getMatchId());
        assertEquals("alice", dto.getSenderUsername());
        assertEquals(NotificationType.MATCH_INVITATION, dto.getNotificationType());
        assertEquals("Match Started", dto.getTitle());
        assertEquals("You have a new match", dto.getMessage());
        assertEquals("SUCCESS", dto.getSubmissionStatus());
        assertEquals(10, dto.getPassedCases());
        assertEquals(20, dto.getTotalCases());
        assertEquals(NotificationMode.EPHEMERAL, dto.getMode());
    }

    @Test
    void test_builder_SetsFieldsCorrectly() {
        MatchNotificationDto dto = MatchNotificationDto.builder()
                .matchId(456L)
                .senderUsername("bob")
                .notificationType(NotificationType.MATCH_STARTED)
                .title("Match Title")
                .message("Match message")
                .submissionStatus("FAILED")
                .passedCases(5)
                .totalCases(10)
                .mode(NotificationMode.PERSISTENT)
                .build();

        assertEquals(456L, dto.getMatchId());
        assertEquals("bob", dto.getSenderUsername());
        assertEquals(NotificationType.MATCH_STARTED, dto.getNotificationType());
        assertEquals("Match Title", dto.getTitle());
        assertEquals("Match message", dto.getMessage());
        assertEquals("FAILED", dto.getSubmissionStatus());
        assertEquals(5, dto.getPassedCases());
        assertEquals(10, dto.getTotalCases());
        assertEquals(NotificationMode.PERSISTENT, dto.getMode());
    }

    @Test
    void test_noArgsConstructor_DefaultsAreNull() {
        MatchNotificationDto dto = new MatchNotificationDto();

        assertNull(dto.getMatchId());
        assertNull(dto.getSubmissionStatus());
        assertNull(dto.getPassedCases());
        assertNull(dto.getTotalCases());
    }

    @Test
    void test_setters_UpdateFieldsCorrectly() {
        MatchNotificationDto dto = new MatchNotificationDto();

        dto.setMatchId(789L);
        dto.setSenderUsername("charlie");
        dto.setNotificationType(NotificationType.MATCH_COMPLETED);
        dto.setTitle("Completed");
        dto.setMessage("Match finished");
        dto.setSubmissionStatus("DONE");
        dto.setPassedCases(15);
        dto.setTotalCases(15);
        dto.setMode(NotificationMode.PERSISTENT);

        assertEquals(789L, dto.getMatchId());
        assertEquals("charlie", dto.getSenderUsername());
        assertEquals(NotificationType.MATCH_COMPLETED, dto.getNotificationType());
        assertEquals("Completed", dto.getTitle());
        assertEquals("Match finished", dto.getMessage());
        assertEquals("DONE", dto.getSubmissionStatus());
        assertEquals(15, dto.getPassedCases());
        assertEquals(15, dto.getTotalCases());
        assertEquals(NotificationMode.PERSISTENT, dto.getMode());
    }

    @Test
    void test_getDestination_ReturnsCorrectTopic() {
        MatchNotificationDto dto = new MatchNotificationDto();
        String destination = dto.getDestination("recipientUser");

        assertEquals("/topic/match-pop/recipientUser", destination);
    }
}
