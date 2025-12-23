import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Board from "../../components/common/Board";
import RejectedProblemRow from "../../components/common/RejectedProblemRow";
import { fetchRejectedProblems } from "../../services/ProblemService";
import { fetchFullProblemById } from "../../services/ProblemService";
interface RejectedProblemRowProps {
  id: number;
  name: string;
  rejectionNote: string;
}

export default function RejectedProblems() {
  const navigate = useNavigate();
  const [problems, setProblems] = useState<RejectedProblemRowProps[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  async function loadRejected(pageToLoad = 0) {
    try {
      // Assuming the backend returns 'rejectionNote' or 'reason' in the object
      const backendPage = await fetchRejectedProblems(pageToLoad, 20);
      const mapped = backendPage.content.map((p: any) => ({
        id: p.id,
        name: p.title ?? p.name,
        rejectionNote: p.rejectionNote || "No reason provided.",
      }));
      setProblems(mapped);
      setPage(pageToLoad);
      setTotalPages(backendPage.totalPages);
    } catch (err) {
      console.error("Failed to fetch rejected problems", err);
    }
  }

  useEffect(() => {
    loadRejected();
  }, []);

  const handlePrevPage = () => {
    if (page > 0) loadRejected(page - 1);
  };

  const handleNextPage = () => {
    if (page < totalPages - 1) loadRejected(page + 1);
  };


  const handleRowClick = async (id: number) => {
  try {
    const problem = await fetchFullProblemById(id);
    console.log(problem);

    // --- 1. Map and Set Problem Info Draft ---
    const problemInfo = {
      id: problem.id,
      solutionLang: problem.solutionLanguage || "",
      timeLimit: problem.timeLimit,
      memoryLimit: problem.memoryLimit,
      rating: problem.rate || "",
      topics: problem.tags ||  [], // Assuming topics is an array of strings/ids
      solutionCode: problem.solutionCode || "",
    };
    localStorage.setItem('problem_info_draft', JSON.stringify(problemInfo));

    // --- 2. Map and Set Problem Statement Draft ---
    const problemStatement = {
      title: problem.title,
      statement: problem.statement,
      inputFormat: problem.inputFormat,
      outputFormat: problem.outputFormat,
      notes: problem.notes,
    };
    localStorage.setItem('problem_statement_draft', JSON.stringify(problemStatement));

    // 3. Map to problem_testcases_draft as an ARRAY
    const testcasesDraft = (problem.visibleTestCases || []).map((tc: any, index: number) => ({
      id: tc.id || `test_${Date.now()}_${index}`,
      input: tc.input,
      visible: tc.visible ?? true,
      actualOutput: tc.output, // Mapping actualOutput to output
    }));

    localStorage.setItem('problem_testcases_draft', JSON.stringify(testcasesDraft));
    
        // --- 4. Navigate to the first page of the multi-step form ---
        navigate(`/add-problem/info`);

      } catch (err) {
        console.error("Failed to prepare problem draft:", err);
        // You might want to show a toast notification here
      }
    };

  return (
    <div className="flex flex-col h-[90vh] p-8 space-y-6">
      <div className="flex-1 overflow-hidden rounded-xl border border-white/5 bg-sidebar/10 shadow-xl">
        <div className="h-full overflow-y-auto custom-scroll">
          <Board<RejectedProblemRowProps & { index: number }>
            data={problems.map((p, index) => ({
              ...p,
              index: index + 1 + page * 20,
            }))}
            columns={["#", "Problem Name", "Rejection Note"]}
           renderRow={(problem) => (
            <RejectedProblemRow
              key={problem.id}
              id={problem.id}
              index={problem.index}
              name={problem.name}
              rejectionNote={problem.rejectionNote}
              onRowClick={() => handleRowClick(problem.id)} // Use the new handler
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
          className="px-5 py-2 bg-sidebar/50 border border-white/10 text-white rounded-full hover:bg-orange hover:border-orange disabled:opacity-30 transition-all font-anta text-sm"
        >
          Previous
        </button>
        <span className="flex items-center text-text/80 font-anta text-sm bg-sidebar/30 px-4 rounded-full border border-white/5">
          Page {page + 1} of {totalPages}
        </span>
        <button
          onClick={handleNextPage}
          disabled={page >= totalPages - 1}
          className="px-5 py-2 bg-sidebar/50 border border-white/10 text-white rounded-full hover:bg-orange hover:border-orange disabled:opacity-30 transition-all font-anta text-sm"
        >
          Next
        </button>
      </div>
    </div>
  );
}