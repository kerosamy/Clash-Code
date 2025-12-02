import { SubmissionStatus } from "../../enums/SubmissionStatus";
import { getSubmissionStatusColor } from "../../utils/getSubmissionStatusColor";
import { getSubmissionStatusDisplay } from "../../utils/getSubmissionStatusDisplay";


export interface SubmissionRowProps {
    submissionStatus: SubmissionStatus;
    timeTaken: number;
    memoryTaken: number;
    submittedAt: string;
    onClick?: () => void;
    className?: string;
}

export default function SubmissionRow({
    submissionStatus,
    timeTaken,
    memoryTaken,
    submittedAt,
    onClick,
    className = "",
}: SubmissionRowProps) {
    const statusColor = getSubmissionStatusColor(submissionStatus);
    
    // Format time (assuming it's in milliseconds)
    const formatTime = (ms: number) => {
        if (ms < 1000) return `${ms}ms`;
        return `${(ms / 1000).toFixed(2)}s`;
    };

    // Format memory (assuming it's in KB)
    const formatMemory = (kb: number) => {
        if (kb < 1024) return `${kb}KB`;
        return `${(kb / 1024).toFixed(2)}MB`;
    };

    // Format date
    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    return (
        <div
            onClick={onClick}
            className={`grid ${className} gap-4 p-4 bg-container hover:bg-sidebar 
                       transition-colors duration-200 cursor-pointer border-b border-gray-700`}
        >
            <div className={`font-semibold font-anta ${statusColor} text-center`}>
                {getSubmissionStatusDisplay(submissionStatus)}
            </div>
            
            <div className="text-text text-center">
                {formatTime(timeTaken)}
            </div>
            
            <div className="text-text text-center">
                {formatMemory(memoryTaken)}
            </div>
            
            <div className="text-text text-container-list text-center">
                {formatDate(submittedAt)}
            </div>
        </div>
    );
}