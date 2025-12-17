package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.MatchNotificationDto;
import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.model.Match;
import com.clashcode.backend.model.Submission;
import com.clashcode.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchNotificationMapperTest {

    private MatchNotificationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MatchNotificationMapper();
    }

    @Test
    void test_mapMatchInvite() {
        User sender = mock(User.class);
        User recipient = mock(User.class);

        when(sender.getUsername()).thenReturn("Caro");
        when(recipient.getUsername()).thenReturn("John");

        MatchNotificationDto dto = mapper.mapMatchInvite(sender, recipient);

        assertEquals(NotificationType.MATCH_INVITATION, dto.getNotificationType());
        assertEquals("Match Invitation", dto.getTitle());
        assertEquals("Caro invites you to a match", dto.getMessage());
        assertEquals("Caro", dto.getSenderUsername());
        assertEquals(NotificationMode.PERSISTENT, dto.getMode());
        assertNull(dto.getMatchId());
    }

    @Test
    void test_mapMatchStarted() {
        Match match = mock(Match.class);
        User recipient = mock(User.class);

        when(match.getId()).thenReturn(101L);
        when(recipient.getUsername()).thenReturn("Micky");

        MatchNotificationDto dto = mapper.mapMatchStarted(match, recipient);

        assertEquals(NotificationType.MATCH_STARTED, dto.getNotificationType());
        assertEquals("Match Started", dto.getTitle());
        assertEquals("Try Your Best, Micky!", dto.getMessage());
        assertEquals("system", dto.getSenderUsername());
        assertEquals(NotificationMode.EPHEMERAL, dto.getMode());
        assertEquals(101L, dto.getMatchId());
    }

    @Test
    void test_mapSubmissionReceived() {
        Match match = mock(Match.class);
        User sender = mock(User.class);
        User recipient = mock(User.class);

        when(match.getId()).thenReturn(202L);
        when(sender.getUsername()).thenReturn("Kero");
        when(recipient.getUsername()).thenReturn("Mina");

        MatchNotificationDto dto = mapper.mapSubmissionReceived(match, sender, recipient);

        assertEquals(NotificationType.SUBMISSION_RECEIVED, dto.getNotificationType());
        assertEquals("Code Submitted", dto.getTitle());
        assertEquals("Kero submitted a solution...", dto.getMessage());
        assertEquals("Kero", dto.getSenderUsername());
        assertEquals(NotificationMode.EPHEMERAL, dto.getMode());
        assertEquals(202L, dto.getMatchId());
    }

    @Test
    void test_mapSubmissionResult() {
        Match match = mock(Match.class);
        Submission submission = mock(Submission.class);
        User recipient = mock(User.class);
        User user = mock(User.class);

        when(match.getId()).thenReturn(303L);
        when(submission.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("Jana");

        when(submission.getStatus()).thenReturn(com.clashcode.backend.enums.SubmissionStatus.ACCEPTED);
        when(submission.getNumberOfPassedTestCases()).thenReturn(5);
        when(submission.getNumberOfTestCases()).thenReturn(5);

        MatchNotificationDto dto = mapper.mapSubmissionResult(match, submission, recipient);

        assertEquals(NotificationType.SUBMISSION_RESULT, dto.getNotificationType());
        assertEquals("Submission Graded", dto.getTitle());
        assertEquals("Jana got ACCEPTED (5/5)", dto.getMessage());
        assertEquals("Jana", dto.getSenderUsername());
        assertEquals(NotificationMode.EPHEMERAL, dto.getMode());
        assertEquals("ACCEPTED", dto.getSubmissionStatus());
        assertEquals(5, dto.getPassedCases());
        assertEquals(5, dto.getTotalCases());
        assertEquals(303L, dto.getMatchId());
    }

}
