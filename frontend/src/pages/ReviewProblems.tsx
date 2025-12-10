import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom"; // Added import
import Board from "../components/common/Board";
import PendingProblemRow from "../components/common/PendingProblemRow";
import {
  fetchPendingProblems,
  approveProblem,
  rejectProblem,
} from "../services/AdminService";

interface PendingProblemRowProps {
  id: number;
  name: string;
  author: string | null;
}

export default function ReviewProblems() {
  const navigate = useNavigate(); // Hook initialized
  const [problems, setProblems] = useState<PendingProblemRowProps[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalProblems, setTotalProblems] = useState(0);

  async function loadPending(pageToLoad = 0) {
    try {
      const backendPage = await fetchPendingProblems(pageToLoad, 20);
      const mapped = backendPage.content.map((p: any) => ({
        id: p.id,
        name: p.title ?? p.name,
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

  const handleReject = async (id: number) => {
    const note = prompt("Enter rejection note:");
    if (!note) return;
    await rejectProblem(id, note);
    loadPending(page);
  };

  const handlePrevPage = () => {
    if (page > 0) loadPending(page - 1);
  };

  const handleNextPage = () => {
    if (page < totalPages - 1) loadPending(page + 1);
  };

  return (
    <div className="flex flex-col h-[90vh] p-8 space-y-6">
      
      {/* Header Section */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-anta text-white tracking-wide">
            Review Problems
          </h1>
          <p className="text-text/60 text-sm mt-1 font-anta">
            Curate and manage community submissions
          </p>
        </div>

        {/* Counter Card */}
        <div className="flex items-center gap-4 bg-sidebar/20 border border-white/5 rounded-xl px-6 py-3 backdrop-blur-sm">
          <div className="flex flex-col items-end">
            <span className="text-2xl font-anta text-orange leading-none">
              {totalProblems}
            </span>
            <span className="text-[10px] font-anta text-text/60 uppercase tracking-widest">
              Pending
            </span>
          </div>
          <div className="h-8 w-[1px] bg-white/10" />
          <div className="h-2 w-2 rounded-full bg-orange animate-pulse shadow-[0_0_10px_rgba(249,115,22,0.5)]" />
        </div>
      </div>

      {/* Table Area */}
      <div className="flex-1 overflow-hidden rounded-xl border border-white/5 bg-sidebar/10 shadow-xl">
        <div className="h-full overflow-y-auto custom-scroll">
          <Board<PendingProblemRowProps & { onApprove: () => void; onReject: () => void }>
            data={problems.map((p, index) => ({
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
                // Pass navigation handlers here
                onRowClick={() => navigate(`/practice/problem/${problem.id}`)}
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
    </div>
  );
}