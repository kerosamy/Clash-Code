// utils/getSubmissionStatusColor.ts
import { SubmissionStatus } from "../enums/SubmissionStatus";

export function getSubmissionStatusColor(status: SubmissionStatus): string {
    switch (status) {
        case SubmissionStatus.ACCEPTED:
            return "text-statusSolved";            // Green (#22C55E)
        case SubmissionStatus.WAITING:
            return "text-text";                    // Gray (#D1D5DB)
        case SubmissionStatus.WRONG_ANSWER:
            return "text-statusUnsolved";          // Red (#EF4444)
        case SubmissionStatus.TIME_LIMIT_EXCEEDED:
            return "text-statusAttempted";         // Yellow (#EAB308)
        case SubmissionStatus.MEMORY_LIMIT_EXCEEDED:
            return "text-statusAttempted";         // Yellow (#EAB308)
        case SubmissionStatus.COMPILATION_ERROR:
            return "text-orange";                  // Orange (#EC7438)
        default:
            return "text-text";                    // Gray (#D1D5DB)
    }
}