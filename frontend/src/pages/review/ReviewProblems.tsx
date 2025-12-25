import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom"; 
import Board from "../../components/common/Board";
import PendingProblemRow from "../../components/common/PendingProblemRow";
import RejectionModal from "../../components/problem/RejectionModal"; // import modal
import {
  fetchPendingProblems,
  approveProblem,
  rejectProblem,
} from "../../services/AdminService";
import { getUsername } from "../../utils/jwtDecoder";
import ReviewHeader from "./ReviewHeader";

interface PendingProblemRowProps {
  id: number;
  name: string;
  index: number;
  author: string | null;
}

export default function ReviewProblems() {
  const navigate = useNavigate(); 
  const [problems, setProblems] = useState<PendingProblemRowProps[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalProblems, setTotalProblems] = useState(0);

  // Modal state
  const [modalOpen, setModalOpen] = useState(false);
  const [currentRejectId, setCurrentRejectId] = useState<number | null>(null);

  async function loadPending(pageToLoad = 0) {
    try {
      const backendPage = await fetchPendingProblems(pageToLoad, 20);
      const mapped = backendPage.content.map((p: any) => ({
        id: p.id,
        name: p.title ?? p.name,
        index: p.index,
        author: p.author,
      }));
      setProblems(mapped);
      setPage(pageToLoad);
      setTotalPages(backendPage.totalPages);
      setTotalProblems(backendPage.totalElements || 0); 
    } catch (err) {
      console.error("Failed to fetch pending problems", err);
    }
  }

  useEffect(() => {
    loadPending();
  }, []);

  const handleApprove = async (id: number) => {
    await approveProblem(id);
    loadPending(page);
  };

  const handleReject = (id: number) => {
    setCurrentRejectId(id);
    setModalOpen(true);
  };

  const handleModalSubmit = async (note: string) => {
    if (currentRejectId !== null) {
      await rejectProblem(currentRejectId, note);
      setModalOpen(false);
      setCurrentRejectId(null);
      loadPending(page);
    }
  };

  const handlePrevPage = () => {
    if (page > 0) loadPending(page - 1);
  };

  const handleNextPage = () => {
    if (page < totalPages - 1) loadPending(page + 1);
  };

  return (
    <div className="flex flex-col h-[90vh] p-8 space-y-6">
      <ReviewHeader title="Review Problems" totalProblems={totalProblems} />
      {/* Table Area */}
      <div className="flex-1 overflow-hidden rounded-xl border border-white/5 bg-sidebar/10 shadow-xl">
        <div className="h-full overflow-y-auto custom-scroll">
          <Board<PendingProblemRowProps & { onApprove: () => void; onReject: () => void }>
            data={problems
              .filter(p => p.author !== getUsername())
              .map((p, index) => ({
              ...p,
              index: index + 1 + page * 20,
              onApprove: () => handleApprove(p.id),
              onReject: () => handleReject(p.id),
            }))}
            columns={["#", "Name", "Author", "", "Approve", "Reject"]}
            renderRow={(problem) => (
              <PendingProblemRow
                key={problem.id}
                id={problem.id}
                index={problem.index}
                name={problem.name}
                author={problem.author}
                onApprove={problem.onApprove}
                onReject={problem.onReject}
                onRowClick={() => navigate(`/review-problems/${problem.id}`)}
                onAuthorClick={() => navigate(`/profile/${problem.author}/overview`)}
              />
            )}
          />
        </div>
      </div>

      {/* Pagination */}
      <div className="flex justify-center gap-4">
        <button
          onClick={handlePrevPage}
          disabled={page === 0}
          className="px-5 py-2 bg-sidebar/50 border border-white/10 text-white rounded-full hover:bg-orange hover:border-orange disabled:opacity-30 disabled:hover:bg-sidebar/50 disabled:hover:border-white/10 disabled:cursor-not-allowed transition-all duration-300 font-anta text-sm"
        >
          Previous
        </button>

        <span className="flex items-center text-text/80 font-anta text-sm bg-sidebar/30 px-4 rounded-full border border-white/5">
          Page {page + 1} of {totalPages}
        </span>

        <button
          onClick={handleNextPage}
          disabled={page >= totalPages - 1}
          className="px-5 py-2 bg-sidebar/50 border border-white/10 text-white rounded-full hover:bg-orange hover:border-orange disabled:opacity-30 disabled:hover:bg-sidebar/50 disabled:hover:border-white/10 disabled:cursor-not-allowed transition-all duration-300 font-anta text-sm"
        >
          Next
        </button>
      </div>

      {/* Rejection Modal */}
      <RejectionModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={handleModalSubmit}
      />
    </div>
  );
}
