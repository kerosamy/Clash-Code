import { SubmissionStatus } from "../../enums/SubmissionStatus";
import { getSubmissionStatusColor } from "../../utils/getSubmissionStatusColor";
import { getSubmissionStatusDisplay } from "../../utils/getSubmissionStatusDisplay";
import { Link } from "react-router-dom";



export interface SubmissionRowProps {
    submissionStatus: SubmissionStatus;
    timeTaken: number;
    memoryTaken: number;
    submittedAt: string;
    numberOfPassedTestCases: number;   
    numberOfTotalTestCases: number;  
    numberOfCurrentTestCase: number; 
    problemTitle: string;
    problemId: number;
    onClick?: () => void;
    className?: string;
}

export default function SubmissionRow({
    submissionStatus,
    timeTaken,
    memoryTaken,
    submittedAt,
    numberOfPassedTestCases,
    numberOfTotalTestCases,
    numberOfCurrentTestCase,
    problemTitle,
    problemId,
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
    const formatMemory = (MB: number) => {
        return `${MB}MB`;
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
                {submissionStatus === SubmissionStatus.RUNNING_ON_TEST
                    ? `Running on Test ${numberOfCurrentTestCase} / ${numberOfTotalTestCases}`
                    : getSubmissionStatusDisplay(submissionStatus)}
            </div>

            <div className="text-text font-semibold text-center">
                <Link
                    to={`/practice/problem/${problemId}`}
                    onClick={(e) => e.stopPropagation()} 
                    className="text-blue-400 hover:text-blue-500 underline"
                >
                    {problemTitle}
                </Link>
            </div>

            
            <div className="text-text text-center">
                {formatTime(timeTaken)}
            </div>
            
            <div className="text-text text-center">
                {formatMemory(memoryTaken)}
            </div>
            <div className="text-text text-center">
                {numberOfPassedTestCases} / {numberOfTotalTestCases}
            </div>
            
            <div className="text-text text-container-list text-center">
                {formatDate(submittedAt)}
            </div>
        </div>
    );
}