// pages/Submissions.tsx
import { useState, useEffect } from "react";
import Board from "../../components/common/Board";
import SubmissionRow from "../../components/common/SubmissionRow";
import { getUserSubmissions } from "../../services/Submissions";
import { SubmissionStatus } from "../../enums/SubmissionStatus";

export interface Submission {
    submissionStatus: SubmissionStatus;
    timeTaken: number;
    memoryTaken: number;
    submittedAt: string;
}

export default function Submissions() {
    const [submissions, setSubmissions] = useState<Submission[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");

    useEffect(() => {
        fetchSubmissions();
    }, []);

    const fetchSubmissions = async () => {
        setLoading(true);
        setError("");
        
        try {
            const data = await getUserSubmissions(1); // User ID = 1 for now
            
            // Convert API response to proper format
            const formattedSubmissions: Submission[] = data.map((item: any) => ({
                submissionStatus: item.submissionStatus,
                timeTaken: item.timeTaken,
                memoryTaken: item.memoryTaken,
                submittedAt: item.submittedAt
            }));
            
            setSubmissions(formattedSubmissions);
        } catch (err) {
            console.error("Failed to fetch submissions:", err);
            setError("Failed to load submissions. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const handleSubmissionClick = (submission: Submission) => {
        console.log("Clicked submission:", submission);
        // TODO: Navigate to submission details or show modal
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center h-screen">
                <div className="text-text text-xl font-anta">Loading submissions...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex flex-col items-center justify-center h-screen">
                <div className="text-statusUnsolved text-xl font-anta mb-4">{error}</div>
                <button 
                    onClick={fetchSubmissions}
                    className="bg-orange hover:bg-orange/90 text-white px-6 py-2 rounded-button font-anta"
                >
                    Retry
                </button>
            </div>
        );
    }

    const columns = ["Status", "Time", "Memory", "Submitted At"];

    return (
        <div className="p-6">
            <div className="mb-6">
                <h1 className="text-3xl font-bold font-anta text-orange">
                    My Submissions
                </h1>
                <p className="text-text mt-2">
                    Total Submissions: {submissions.length}
                </p>
            </div>
            
            <Board<Submission>
                data={submissions}
                columns={columns}
                onRowClick={handleSubmissionClick}
                gridCols="grid-cols-[200px_150px_150px_1fr]"
                renderRow={(submission, onClick) => (
                    <SubmissionRow
                        key={`${submission.submittedAt}-${submission.timeTaken}`}
                        submissionStatus={submission.submissionStatus}
                        timeTaken={submission.timeTaken}
                        memoryTaken={submission.memoryTaken}
                        submittedAt={submission.submittedAt}
                        onClick={onClick}
                        className="grid-cols-[200px_150px_150px_1fr]"
                    />
                )}
            />
        </div>
    );
}