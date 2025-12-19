package com.clashcode.backend.mapper;

import com.clashcode.backend.Notification.Dtos.MatchNotificationDto;
import com.clashcode.backend.enums.NotificationMode;
import com.clashcode.backend.enums.NotificationType;
import com.clashcode.backend.model.Match;
import com.clashcode.backend.model.Submission;
import com.clashcode.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class MatchNotificationMapper {

    public MatchNotificationDto mapMatchInvite(User sender) {
        return MatchNotificationDto.builder()
                .notificationType(NotificationType.MATCH_INVITATION)
                .title("Match Invitation")
                .message(sender.getUsername() + " invites you to a match")
                .senderUsername(sender.getUsername())
                .mode(NotificationMode.PERSISTENT)
                .build();
    }

    public MatchNotificationDto mapMatchStarted(Match match, User recipient) {
        return MatchNotificationDto.builder()
                .matchId(match.getId())
                .senderUsername("system")
                .notificationType(NotificationType.MATCH_STARTED)
                .title("Match Started")
                .message(String.format("Try Your Best, %s!", recipient.getUsername()))
                .mode(NotificationMode.EPHEMERAL)
                .build();
    }

    public MatchNotificationDto mapSubmissionReceived(Match match, User sender) {
        return MatchNotificationDto.builder()
                .matchId(match.getId())
                .senderUsername(sender.getUsername())
                .notificationType(NotificationType.SUBMISSION_RECEIVED)
                .title("Code Submitted")
                .message(sender.getUsername() + " submitted a solution...")
                .mode(NotificationMode.EPHEMERAL)
                .build();
    }

    public MatchNotificationDto mapSubmissionResult(Match match, Submission submission) {
        return MatchNotificationDto.builder()
                .matchId(match.getId())
                .senderUsername(submission.getUser().getUsername())
                .notificationType(NotificationType.SUBMISSION_RESULT)
                .title("Submission Graded")
                .message(String.format("%s got %s (%d/%d)",
                        submission.getUser().getUsername(),
                        submission.getStatus(),
                        submission.getNumberOfPassedTestCases(),
                        submission.getNumberOfTestCases()))
                .submissionStatus(submission.getStatus().toString())
                .passedCases(submission.getNumberOfPassedTestCases())
                .totalCases(submission.getNumberOfTestCases())
                .mode(NotificationMode.EPHEMERAL)
                .build();
    }

    public MatchNotificationDto mapMatchEnded(Match match) {
        return MatchNotificationDto.builder()
                .matchId(match.getId())
                .senderUsername("system")
                .notificationType(NotificationType.MATCH_COMPLETED)
                .title("Match Completed")
                .message("well done!")
                .mode(NotificationMode.EPHEMERAL)
                .build();
    }
}
