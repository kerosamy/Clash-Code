package com.clashcode.backend.mapper;

import com.clashcode.backend.Notification.Dtos.MatchNotificationDto;
import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.enums.SubmissionStatus;
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
        when(sender.getUsername()).thenReturn("Caro");

        MatchNotificationDto dto = mapper.mapMatchInvite(sender);

        assertEquals(NotificationType.MATCH_INVITATION, dto.getNotificationType());
        assertEquals("Match Invitation", dto.getTitle());
        assertEquals("Caro invites you to a match", dto.getMessage());
        assertEquals("Caro", dto.getSenderUsername());
        assertEquals(NotificationMode.PERSISTENT, dto.getMode());
        assertNull(dto.getMatchId());
    }

    @Test
    void test_mapMatchInvitationCanceled() {
        User sender = mock(User.class);
        when(sender.getUsername()).thenReturn("Alice");

        MatchNotificationDto dto = mapper.mapMatchInvitationCanceled(sender);

        assertEquals(NotificationType.MATCH_INVITATION_CANCELED, dto.getNotificationType());
        assertEquals("Match Invitation Canceled", dto.getTitle());
        assertEquals("Alice has canceled their match invitation", dto.getMessage());
        assertEquals("Alice", dto.getSenderUsername());
        assertEquals(NotificationMode.EPHEMERAL, dto.getMode());
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

        when(match.getId()).thenReturn(202L);
        when(sender.getUsername()).thenReturn("Kero");

        MatchNotificationDto dto = mapper.mapSubmissionReceived(match, sender);

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
        User user = mock(User.class);

        when(match.getId()).thenReturn(303L);
        when(submission.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("Jana");
        when(submission.getStatus()).thenReturn(SubmissionStatus.ACCEPTED);
        when(submission.getNumberOfPassedTestCases()).thenReturn(5);
        when(submission.getNumberOfTestCases()).thenReturn(5);

        MatchNotificationDto dto = mapper.mapSubmissionResult(match, submission);

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

    @Test
    void test_mapSubmissionResult_wrongAnswer() {
        Match match = mock(Match.class);
        Submission submission = mock(Submission.class);
        User user = mock(User.class);

        when(match.getId()).thenReturn(404L);
        when(submission.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("Bob");
        when(submission.getStatus()).thenReturn(SubmissionStatus.WRONG_ANSWER);
        when(submission.getNumberOfPassedTestCases()).thenReturn(3);
        when(submission.getNumberOfTestCases()).thenReturn(5);

        MatchNotificationDto dto = mapper.mapSubmissionResult(match, submission);

        assertEquals("Bob got WRONG_ANSWER (3/5)", dto.getMessage());
        assertEquals("WRONG_ANSWER", dto.getSubmissionStatus());
        assertEquals(3, dto.getPassedCases());
    }

    @Test
    void test_mapMatchEnded() {
        Match match = mock(Match.class);
        when(match.getId()).thenReturn(404L);

        MatchNotificationDto dto = mapper.mapMatchEnded(match);

        assertEquals(404L, dto.getMatchId());
        assertEquals("system", dto.getSenderUsername());
        assertEquals(NotificationType.MATCH_COMPLETED, dto.getNotificationType());
        assertEquals("Match Completed", dto.getTitle());
        assertEquals("well done!", dto.getMessage());
        assertEquals(NotificationMode.EPHEMERAL, dto.getMode());
    }

    @Test
    void test_mapOpponentResigned() {
        Match match = mock(Match.class);
        User resigningUser = mock(User.class);
        String username = "Quitter123";

        when(match.getId()).thenReturn(505L);
        when(resigningUser.getUsername()).thenReturn(username);

        MatchNotificationDto dto = mapper.mapOpponentResigned(match, resigningUser);

        assertEquals(505L, dto.getMatchId());
        assertEquals(username, dto.getSenderUsername());
        assertEquals(NotificationType.USER_RESIGNED, dto.getNotificationType());
        assertEquals("Opponent Resigned", dto.getTitle());
        assertEquals(username + " has resigned. You win the match!", dto.getMessage());
        assertEquals(NotificationMode.EPHEMERAL, dto.getMode());
    }
}