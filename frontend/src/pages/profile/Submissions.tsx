import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Board from "../../components/common/Board";
import SubmissionRow from "../../components/common/SubmissionRow";
import { getUserSubmissions  , getSubmissionStatus} from "../../services/SubmissionsService";
import { SubmissionStatus } from "../../enums/SubmissionStatus";
import SubmissionDetails from "../../components/common/SubmissionDetails";
import LogoLoader from "../../components/Loader/LogoLoader";
import { waitForLoader } from "../../components/Loader/WaitLoader";
import { getUsername } from "../../utils/jwtDecoder";

export interface Submission {
    submissionId: number;
    submissionStatus: SubmissionStatus;
    timeTaken: number;  
    memoryTaken: number;
    submittedAt: string;
    problemTitle: string;
    problemId: number;
    numberOfPassedTestCases: number;
    numberOfTotalTestCases: number;
    numberOfCurrentTestCase: number;
}

export default function Submissions() {
    const params = useParams();
    const matchIdString = params.matchId || params.id;
    const [submissions, setSubmissions] = useState<Submission[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const [selectedSubmissionId, setSelectedSubmissionId] = useState<number | null>(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const { username: paramUsername } = useParams<{ username?: string }>();
    const navigate = useNavigate();

useEffect(() => {
    fetchSubmissions();
}, [matchIdString]);

useEffect(() => {
   
    const pending = submissions.filter(
        (s) =>
            s.submissionStatus === SubmissionStatus.WAITING ||
            s.submissionStatus === SubmissionStatus.RUNNING_ON_TEST
    );
    console.log("Pending submissions for polling:", pending);
    if (pending.length === 0) return;

    const interval = setInterval(async () => {
        console.log("Polling pending submissions...");

        const updatedSubmissions = await Promise.all(
            submissions.map(async (s) => {
                if (
                    s.submissionStatus !== SubmissionStatus.WAITING &&
                    s.submissionStatus !== SubmissionStatus.RUNNING_ON_TEST
                ) {
                    return s; // No need to poll
                }

                try {
                    const updated = await getSubmissionStatus(s.submissionId);
                    return {
                        ...s,
                        ...updated,
                        submissionStatus: SubmissionStatus[updated.submissionStatus as keyof typeof SubmissionStatus],
                    };
                } catch (err) {
                    console.error("Polling error:", err);
                    return s;
                }
            })
        );

        setSubmissions(updatedSubmissions);
    }, 2000); // Poll every 2 seconds

    return () => clearInterval(interval);
}, [submissions]);


    const fetchSubmissions = async () => {
        if(paramUsername != getUsername()){
            navigate('/not-found', { replace: true });
        }
        setLoading(true);
        setError("");
        const startTime = Date.now();
        
        try {
            const data = await getUserSubmissions();
            await waitForLoader(startTime);
            
            let formattedSubmissions: Submission[] = data.map((item: any) => ({
                submissionId: item.submissionId,
                submissionStatus:  SubmissionStatus[item.submissionStatus as keyof typeof SubmissionStatus] ,
                timeTaken: item.timeTaken,
                memoryTaken: item.memoryTaken,
                submittedAt: item.submittedAt,
                numberOfPassedTestCases: item.numberOfPassedTestCases,
                numberOfTotalTestCases: item.numberOfTotalTestCases,
                numberOfCurrentTestCase: item.numberOfCurrentTestCase,
                problemTitle: item.problemTitle,
                problemId: item.problemId,
            }));
            console.log("Fetched submissions:", formattedSubmissions);
            
            setSubmissions(formattedSubmissions);
        } catch (err) {
            console.error("Failed to fetch submissions:", err);
            setError("Failed to load submissions. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const handleSubmissionClick = (submission: Submission) => {
        setSelectedSubmissionId(submission.submissionId);
        setIsModalOpen(true);
    };


    if (loading) {
        return <LogoLoader loadingMessage="Loading Submissions"/>;
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

    const columns = ["Status", "Problem", "Time", "Memory", "Passed", "Submitted At"];

    return (
        <div className="p-6">
            <div className="mb-6 ">
                <h1 className="text-3xl font-bold font-anta text-orange text-center p-4">
                    {"My Submissions"}
                </h1>
                <p className="text-text mt-2">
                    Total Submissions: {submissions.length}
                </p>
            </div>
                        
            <Board<Submission>
                data={submissions}
                columns={columns}
                onRowClick={handleSubmissionClick}
                gridCols="grid-cols-[200px_200px_150px_150px_150px_1fr]"

                renderRow={(submission, onClick) => (
                    <SubmissionRow
                        key={`${submission.submittedAt}-${submission.timeTaken}`}
                        submissionStatus={submission.submissionStatus}
                        timeTaken={submission.timeTaken}
                        memoryTaken={submission.memoryTaken}
                        submittedAt={submission.submittedAt}
                        numberOfPassedTestCases={submission.numberOfPassedTestCases}
                        numberOfTotalTestCases={submission.numberOfTotalTestCases}
                        numberOfCurrentTestCase={submission.numberOfCurrentTestCase}
                        problemTitle={submission.problemTitle}
                        problemId={submission.problemId}
                        onClick={onClick}
                        className="grid-cols-[200px_200px_150px_150px_150px_1fr]"
                    />
                )}
            />
            {selectedSubmissionId !== null && (
                <SubmissionDetails
                    submissionId={selectedSubmissionId}
                    isOpen={isModalOpen}
                    onClose={() => {
                        setIsModalOpen(false);
                        setSelectedSubmissionId(null);
                    }}
                />
            )}
        </div>
    );
}