import { SubmissionStatus } from "../enums/SubmissionStatus";

export function getSubmissionStatusDisplay(status: SubmissionStatus): string {
    switch (status) {
        case SubmissionStatus.ACCEPTED:
            return 'Accepted';
        case SubmissionStatus.WAITING:
            return 'Waiting';
        case SubmissionStatus.WRONG_ANSWER:
            return 'Wrong Answer';
        case SubmissionStatus.TIME_LIMIT_EXCEEDED:
            return 'Time Limit Exceeded';
        case SubmissionStatus.MEMORY_LIMIT_EXCEEDED:
            return 'Memory Limit Exceeded';
        case SubmissionStatus.COMPILATION_ERROR:
            return 'Compilation Error';
        default:
            return 'Unknown';
    }
}