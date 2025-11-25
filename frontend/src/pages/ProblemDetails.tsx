import { useEffect, useState } from "react";
import { data, useParams } from "react-router-dom";
import ProblemSection from "../components/ProblemSectionProps";
import TestCases from "../components/TestCase";
import { fetchProblemById } from "../services/problem.service";

export default function ProblemDetails() {
  const { id } = useParams<{ id: string }>(); // useParams always returns string
  const problemId = Number(id); // convert to integer
  const [problem, setProblem] = useState<any | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!problemId) return;
    fetchProblemById(problemId)
      .then((data) => setProblem(data))
      .finally(() => setLoading(false));
  }, [problemId]);

  if (loading) return <p>Loading...</p>;
  if (!problem) return <p>Problem not found</p>;

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
