import { useParams } from "react-router-dom";
import ProblemSection from "../components/ProblemSectionProps";
import TestCases from "../components/TestCase";

export default function ProblemDetails() {
  const { id } = useParams();

  // Dummy problem data
  const problem = {
    title: "A. Watermelon",
    timeLimit: "1 second",
    memoryLimit: "256 MB",
    statement:
      "You are given a watermelon. You need to check if you can split it into two parts such that each part weighs an even number of kilos. " +
      "You are given a watermelon. You need to check if you can split it into two parts such that each part weighs an even number of kilos. " +
      "You are given a watermelon. You need to check if you can split it into two parts such that each part weighs an even number of kilos.",
    inputFormat:
      "Single integer w (1 ≤ w ≤ 100) — the weight of the watermelon.",
    outputFormat:
      "Print 'YES' if the watermelon can be split. Otherwise, print 'NO'.",
    testcases: [
      { input: "8 \n1 2 3 4 5 6 7 8 ", output: "YES" },
      { input: "3", output: "NO" },
      { input: "10", output: "YES" },
    ],
    notes: "The weight must be divided into two positive integers.",
  };

  return (
    <div className="w-full text-white font-anta">
      {/* Content Container */}
      <div className="max-w-6xl mx-auto w-full">
        {/* Title + Limits */}
        <div className="mb-8 text-center">
          <h1 className="text-3xl mb-3">{problem.title}</h1>
          <div className="text-sm text-text space-y-1">
            <p>Time Limit: {problem.timeLimit}</p>
            <p>Memory Limit: {problem.memoryLimit}</p>
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
        <TestCases testcases={problem.testcases} />

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