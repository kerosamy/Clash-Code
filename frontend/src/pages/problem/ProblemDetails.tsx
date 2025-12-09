import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import ProblemSection from "../../components/problem/ProblemSectionProps";
import TestCases from "../../components/problem/TestCase";
import Loading from "../../components/Loading";
import { fetchProblemById } from "../../services/ProblemService";

export default function ProblemDetails() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const problemId = Number(id);
  const [problem, setProblem] = useState<any | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!problemId || isNaN(problemId)) {
      navigate("/not-found", { replace: true });
      return;
    }

    fetchProblemById(problemId)
      .then((data) => {
        if (!data) {
          navigate("/not-found", { replace: true });
        } else {
          setProblem(data);
        }
      })
      .catch(() => {
        navigate("/not-found", { replace: true });
      })
      .finally(() => setLoading(false));
  }, [problemId, navigate]);

  if (loading) {
    return <Loading message="Loading problem..." />;
  }

  if (!problem) {
    return null;
  }

  return (
    <div className="w-full text-white font-anta">
      <div className="max-w-6xl mx-auto w-full">
        {/* Title + Limits */}
        <div className="mb-8 text-center">
          <h1 className="text-3xl mb-3">{problem.title}</h1>
          <div className="text-sm text-text space-y-1">
            <p>Time Limit Per Test: {problem.timeLimit} MS</p>
            <p>Memory Limit Per Test: {problem.memoryLimit} MB</p>
          </div>
        </div>

        {/* Problem Statement */}
        <ProblemSection header="Problem Statement">
          <p>{problem.statement}</p>
        </ProblemSection>

        {/* Input Format */}
        <ProblemSection header="Input Format">
          <p>{problem.inputFormat}</p>
        </ProblemSection>

        {/* Output Format */}
        <ProblemSection header="Output Format">
          <p>{problem.outputFormat}</p>
        </ProblemSection>

        {/* Test Cases */}
        <TestCases testcases={problem.visibleTestCases || []} />

        {/* Notes */}
        {problem.notes && (
          <ProblemSection header="Notes">
            <p>{problem.notes}</p>
          </ProblemSection>
        )}
      </div>
    </div>
  );
}