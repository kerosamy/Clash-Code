import CounterCard from '../../components/common/CounterCard';
import { useEffect, useState } from "react";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import ProblemSection from "../../components/problem/ProblemSection";
import TestCases from "../../components/problem/TestCase";
import { waitForLoader } from "../../components/Loader/WaitLoader";

import { fetchProblemById } from "../../services/ProblemService";
import { getProblemForMatch } from "../../services/MatchService";
import TitleAndLimitsSection from "../../components/problem/TitleAndLimitsSection";

export default function ReviewProblem() {
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
 
  return (<div className='flex-1'>
    <div className="flex flex-col  p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-anta text-white tracking-wide">
            Review Problem
          </h1>
          <p className="text-text/60 text-sm mt-1 font-anta">
            Curate and manage community submissions
          </p>
        </div>
        <div>
            
          <CounterCard count={1} />
        </div>
      </div>
    </div>
    { problem && <div className="w-full py-0 text-white font-anta p-scroll-x">
        <div className="max-w-6xl mx-auto w-full">
            <TitleAndLimitsSection 
                title={problem.title}
                timeLimit={problem.timeLimit}
                memoryLimit={problem.memoryLimit}
            />

            <ProblemSection header="Problem Statement">
            <p>{problem.statement}</p>
            </ProblemSection>

            <ProblemSection header="Input Format">
            <p>{problem.inputFormat}</p>
            </ProblemSection>

            <ProblemSection header="Output Format">
            <p>{problem.outputFormat}</p>
            </ProblemSection>

            <TestCases testcases={problem.visibleTestCases || []} />

            {problem.notes && (
            <ProblemSection header="Notes">
                <p>{problem.notes}</p>
            </ProblemSection>
            )}
        </div>
      </div>
    }
  </div>);
}