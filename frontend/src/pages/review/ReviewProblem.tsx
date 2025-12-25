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
        if (!data) {
          navigate("/not-found", { replace: true });
        } else {
          await waitForLoader(startTime);
          setProblem(data);
        }
      })
      .catch(() => navigate("/not-found", { replace: true }))
      .finally(() => setLoading(false));
  }, [numericId, navigate]);

  /* ✅ FIXED FUNCTION */
  const handleAIRequest = async () => {
    if (loadingAI) return;

    setLoadingAI(true);
    setErrorAI(null);

    try {
      const responseText = await fetchAIReview(numericId);

      if (!responseText) {
        throw new Error("Empty AI response");
      }

      const parsedData =
        typeof responseText === "string"
          ? JSON.parse(responseText)
          : responseText;

      setAiReview(parsedData);
      setShowFullReport(true);
    } catch (err: any) {
      console.error("AI Review failed", err);
      setErrorAI(err?.message || "An unexpected error occurred during AI analysis.");
    } finally {
      setLoadingAI(false);
    }
  };

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
            isOpen
              ? "bg-orange border-orange text-black"
              : "bg-orange/10 border-orange/30 text-orange"
          }`}
        >
          <Sparkles size={16} className={isOpen ? "animate-spin-slow" : ""} />
        </button>

        {isOpen && (
          <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-4 w-96 p-6 rounded-2xl bg-slate-900/95 border border-white/20 shadow-[0_20px_50px_rgba(0,0,0,0.5)] backdrop-blur-xl z-[100]">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h4 className="text-[10px] font-black text-orange uppercase tracking-[0.2em] mb-1">
                  AI Technical Audit
                </h4>
                <p className="text-sm font-bold text-white">{category}</p>
              </div>
              <div className={`px-3 py-1 rounded-full border text-sm font-black ${getScoreColor(details.score)}`}>
                {details.score} / 5
              </div>
            </div>
            <p className="text-[13px] text-text/90 leading-relaxed font-medium">
              {details.comment}
            </p>
          </div>
        )}
      </div>
    );
  };

  const getAverageScore = () => {
    if (!aiReview) return 0;
    const scores = Object.values(aiReview).map((d) => d.score);
    return scores.reduce((a, b) => a + b, 0) / scores.length;
  };

  return (
    <div className="flex-1">
      <ReviewHeader
        title="Review Problem"
        onApprove={async () => {
          await approveProblem(numericId);
          navigate("/review-problems");
        }}
        onReject={() => setModalOpen(true)}
        onAIReview={handleAIRequest}
      />

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
