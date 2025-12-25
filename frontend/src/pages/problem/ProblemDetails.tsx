import { useEffect, useState } from "react";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import ProblemSection from "../../components/problem/ProblemSection";
import TestCases from "../../components/problem/TestCase";
import LogoLoader from "../../components/Loader/LogoLoader";
import { waitForLoader } from "../../components/Loader/WaitLoader";

import { fetchProblemById } from "../../services/ProblemService";
import { getProblemForMatch } from "../../services/MatchService";
import TitleAndLimitsSection from "../../components/problem/TitleAndLimitsSection";

export default function ProblemDetails() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const location = useLocation();

  const numericId = Number(id);
  const [problem, setProblem] = useState<any | null>(null);
  const [loading, setLoading] = useState(true);

  const mode: "practice" | "match" = location.pathname.startsWith("/practice")
    ? "practice"
    : "match";

  useEffect(() => {
    if (!numericId || isNaN(numericId)) {
      navigate("/not-found", { replace: true });
      return;
    }

    const startTime = Date.now();

    const fetchData =
      mode === "practice"
        ? fetchProblemById(numericId)
        : getProblemForMatch(numericId);

    fetchData
      .then(async (data) => {
        if (!data) {
          navigate("/not-found", { replace: true });
        } else {
          if (mode === "practice") {
            await waitForLoader(startTime);
          }
          setProblem(data);
        }
      })
      .catch(() => {
        navigate("/not-found", { replace: true });
      })
      .finally(() => setLoading(false));
  }, [numericId, navigate, mode]);

  if (loading) {
    return (
      <div className="flex flex-col flex-1 font-anta">
        <LogoLoader loadingMessage="Loading Problem" />
      </div>
    );
  }

  if (!problem) {
    return null;
  }
 
  return (
    <div className="w-full text-white font-anta my-8 p-scroll-x">
      <div className="max-w-6xl mx-auto w-full">
        {/* Title + Limits */}
        <TitleAndLimitsSection 
          title={problem.title}
          timeLimit={problem.timeLimit}
          memoryLimit={problem.memoryLimit}
        />

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