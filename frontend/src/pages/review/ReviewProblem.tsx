import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import TestCases from "../../components/problem/TestCase";
import { waitForLoader } from "../../components/Loader/WaitLoader";
import { fetchProblemById } from "../../services/ProblemService";
import { approveProblem, rejectProblem, fetchAIReview } from "../../services/AdminService";
import TitleAndLimitsSection from "../../components/problem/TitleAndLimitsSection";
import ReviewHeader from "./ReviewHeader";
import RejectionModal from "../../components/problem/RejectionModal";
import { ChevronDown, ChevronUp, Sparkles } from "lucide-react";
import LogoLoader from "../../components/Loader/LogoLoader";

interface AIReviewDetail {
  score: number;
  comment: string;
}

interface AIReviewData {
  [key: string]: AIReviewDetail;
}

export default function ReviewProblem() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const numericId = Number(id);
  const [errorAI, setErrorAI] = useState<string | null>(null);
  const [problem, setProblem] = useState<any | null>(null);
  const [loading, setLoading] = useState(true);
  const [loadingAI, setLoadingAI] = useState(false);
  const [aiReview, setAiReview] = useState<AIReviewData | null>(null);
  const [showFullReport, setShowFullReport] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);

  useEffect(() => {
    if (!numericId || isNaN(numericId)) {
      navigate("/not-found", { replace: true });
      return;
    }
    setLoading(true);
    const startTime = Date.now();
    fetchProblemById(numericId)
      .then(async (data) => {
        if (!data) navigate("/not-found", { replace: true });
        else {
          await waitForLoader(startTime);
          setProblem(data);
        }
      })
      .catch(() => navigate("/not-found", { replace: true }))
      .finally(() => setLoading(false));
  }, [numericId, navigate]);

  const handleAIRequest = async () => {
    if (loadingAI) return;
    
    setLoadingAI(true);
    setErrorAI(null); // Reset error state on new attempt
    
    try {
      const responseText = await fetchAIReview(numericId);
      // Validate that we got a response
    if (responseText) {
      const parsedData = typeof responseText === "string" ? JSON.parse(responseText) : responseText;
      setAiReview(parsedData);
      

      window.location.reload();
      setAiReview(parsedData);
      setShowFullReport(true);
    } }catch (err: any) {
      console.error("AI Review failed", err);
      // Capture the error message as a string
      setErrorAI(err.message || "An unexpected error occurred during AI analysis.");
    } finally {
      setLoadingAI(false);
    }
  

  const getScoreColor = (score: number) => {
    if (score >= 4) return "text-green-400 border-green-400/20 bg-green-400/5";
    if (score >= 2) return "text-yellow-400 border-yellow-400/20 bg-yellow-400/5";
    return "text-red-400 border-red-400/20 bg-red-400/5";
  };

  if (loading) {
    return (
      <div className="flex flex-col h-screen font-anta">
        <LogoLoader loadingMessage="Loading Problems" />
      </div>
    );
  }

  const InlineAICard = ({ category }: { category: string }) => {
    const [isOpen, setIsOpen] = useState(false);
    const details = aiReview?.[category];
    if (!details) return null;

    return (
      <div className="relative inline-flex items-center ml-4 align-middle">
        <button
          onMouseEnter={() => setIsOpen(true)}
          onMouseLeave={() => setIsOpen(false)}
          onClick={() => setIsOpen(!isOpen)}
          className={`p-1.5 rounded-lg border transition-all duration-300 ${
            isOpen ? "bg-orange border-orange text-black" : "bg-orange/10 border-orange/30 text-orange"
          }`}
        >
          <Sparkles size={16} className={isOpen ? "animate-spin-slow" : ""} />
        </button>

        {isOpen && (
          <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-4 w-96 p-6 rounded-2xl bg-slate-900/95 border border-white/20 shadow-[0_20px_50px_rgba(0,0,0,0.5)] backdrop-blur-xl z-[100] animate-in fade-in zoom-in-95 slide-in-from-bottom-2 duration-200">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h4 className="text-[10px] font-black text-orange uppercase tracking-[0.2em] mb-1">AI Technical Audit</h4>
                <p className="text-sm font-bold text-white">{category}</p>
              </div>
              <div className={`px-3 py-1 rounded-full border text-sm font-black ${getScoreColor(details.score)}`}>
                {details.score} / 5
              </div>
            </div>
            <p className="text-[13px] text-text/90 leading-relaxed font-medium">{details.comment}</p>
            <div className="absolute -bottom-2 left-1/2 -translate-x-1/2 w-4 h-4 bg-slate-900 border-r border-b border-white/20 rotate-45"></div>
          </div>
        )}
      </div>
    );
  };

  // Helper to calculate average score
  const getAverageScore = () => {
    if (!aiReview) return 0;
    const scores = Object.values(aiReview).map((d) => d.score);
    return scores.reduce((a, b) => a + b, 0) / scores.length;
  };

  return (
    <div className="flex-1">
      <div className="flex flex-col p-8 space-y-6">
        <ReviewHeader
          title="Review Problem"
          onApprove={async () => {
            await approveProblem(numericId);
            navigate("/review-problems");
          }}
          onReject={() => setModalOpen(true)}
          onAIReview={handleAIRequest}
        />
      </div>

      <div className="w-full py-0 text-white font-anta p-scroll-x">
        <div className="max-w-6xl mx-auto w-full px-8 pb-10">
          
        {/* AI QUALITY SCORE SECTION */}
{(loadingAI || aiReview || errorAI) && (
  <div className="mb-12 p-6 rounded-xl border border-white/10 bg-sidebar/20 shadow-inner relative overflow-hidden">
    
    {/* Error State View */}
    {errorAI && !loadingAI && (
      <div className="flex flex-col items-center justify-center py-4 text-center">
        <div className="bg-red-500/10 border border-red-500/20 p-4 rounded-lg">
          <p className="text-red-400 text-sm font-bold mb-1">AI Analysis Failed</p>
          <p className="text-red-400/70 text-xs font-anta">{errorAI}</p>
          <button 
            onClick={handleAIRequest}
            className="mt-3 text-[10px] uppercase tracking-widest text-white underline underline-offset-4 hover:text-orange"
          >
            Try Again
          </button>
        </div>
      </div>
    )}
              <div className="flex justify-between items-center">
                <div className="flex items-center gap-6">
                  <div className="relative">
                    <svg className="w-16 h-16 transform -rotate-90">
                      <circle cx="32" cy="32" r="28" stroke="currentColor" strokeWidth="4" fill="transparent" className="text-white/5" />
                      <circle
                        cx="32" cy="32" r="28" stroke="currentColor" strokeWidth="4" fill="transparent"
                        strokeDasharray={175.9}
                        strokeDashoffset={175.9 - 175.9 * (aiReview ? getAverageScore() / 5 : 0)}
                        className="text-orange transition-all duration-1000"
                      />
                    </svg>
                    <div className="absolute inset-0 flex items-center justify-center text-lg font-bold">
                      {aiReview ? getAverageScore().toFixed(1) : "..."}
                    </div>
                  </div>
                  <div>
                    <h3 className="font-anta text-xl tracking-wide">AI Quality Score</h3>
                    <p className="text-xs text-text/50 uppercase tracking-widest">Global evaluation metrics</p>
                  </div>
                </div>
                <button
                  onClick={() => setShowFullReport(!showFullReport)}
                  className="flex items-center gap-2 px-4 py-2 rounded-full border border-white/10 hover:bg-white/5 transition-all text-xs uppercase tracking-widest"
                >
                  {showFullReport ? <><ChevronUp size={14} /> Hide Summary</> : <><ChevronDown size={14} /> View Summary</>}
                </button>
              </div>

              {showFullReport && aiReview && (
                <div className="mt-8 grid grid-cols-1 md:grid-cols-5 gap-4 border-t border-white/5 pt-8 animate-in fade-in slide-in-from-top-2">
                  {Object.entries(aiReview).map(([category, details]) => (
                    <div key={category} className="text-center">
                      <div className={`text-xl font-bold mb-1 ${getScoreColor(details.score)}`}>{details.score}/5</div>
                      <div className="text-[10px] text-text/40 uppercase font-anta">{category}</div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {problem && (
            <div className="space-y-16">
              {/* TITLE SECTION */}
              <div className="w-full flex justify-center">
                <TitleAndLimitsSection
                  title={
                    <div className="flex items-center justify-center gap-2">
                      <span className="text-center">{problem.title}</span>
                      <InlineAICard category="Constraints Correctness" />
                    </div>
                  }
                  timeLimit={problem.timeLimit}
                  memoryLimit={problem.memoryLimit}
                />
              </div>

              {/* STATEMENT */}
              <div className="space-y-4">
                <div className="flex items-center">
                  <h2 className="text-2xl font-bold tracking-tight">Problem Statement</h2>
                  <InlineAICard category="Statement Clarity" />
                </div>
                <div className="p-8 rounded-2xl bg-white/[0.03] border border-white/10 text-lg leading-relaxed text-text/90">
                  {problem.statement}
                </div>
              </div>

              {/* SPECS */}
              <div className="space-y-4">
                <div className="flex items-center">
                  <h2 className="text-2xl font-bold tracking-tight">Input/Output Format</h2>
                  <InlineAICard category="Input/Output Format Clarity" />
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="p-6 rounded-2xl bg-white/[0.02] border border-white/5">
                    <h4 className="text-xs font-black text-orange uppercase tracking-[0.2em] mb-4">Expected Input</h4>
                    <p className="text-text/70">{problem.inputFormat}</p>
                  </div>
                  <div className="p-6 rounded-2xl bg-white/[0.02] border border-white/5">
                    <h4 className="text-xs font-black text-orange uppercase tracking-[0.2em] mb-4">Expected Output</h4>
                    <p className="text-text/70">{problem.outputFormat}</p>
                  </div>
                </div>
              </div>

              {/* TEST CASES */}
              <div className="space-y-4">
                <div className="flex items-center">
                  <h2 className="text-2xl font-bold tracking-tight">Validation Test Cases</h2>
                  <InlineAICard category="Solution Correctness" />
                </div>
                <TestCases testcases={problem.visibleTestCases || []} />
              </div>
            </div>
          )}
        </div>
      </div>

      <RejectionModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={async (note) => {
          await rejectProblem(numericId, note);
          navigate("/review-problems");
        }}
      />
    </div>
  );
}