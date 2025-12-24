import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Board from "../../components/common/Board";
import SuggestionRow from "../../components/common/SuggestionRow";
import SingleSelectDropdown from "../../components/common/SingleSelectDropDown";
import { fetchMySuggestions } from "../../services/ProblemService";
import { fetchFullProblemById } from "../../services/ProblemService";

export interface SuggestionRowProps {
  id: number;
  name: string;
  status: string;
  rejectionNote: string;
}

export default function MySuggestions() {
  const navigate = useNavigate();
  const [problems, setProblems] = useState<SuggestionRowProps[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [statusFilter, setStatusFilter] = useState<string | null>(null);

  const STATUS_OPTIONS = ["PENDING_APPROVAL", "APPROVED", "REJECTED"];

  async function loadSuggestions(
    pageToLoad = 0,
    status: string | null = statusFilter
  ) {
    try {
      const backendPage = await fetchMySuggestions(pageToLoad, 20, status);

      const mapped = backendPage.content.map((p: any) => ({
        id: p.id,
        name: p.title ?? p.name,
        status: p.problemStatus || p.status,
        rejectionNote: p.rejectionNote || "-",
      }));

      setProblems(mapped);
      setPage(pageToLoad);
      setTotalPages(backendPage.totalPages);
    } catch (err) {
      console.error("Failed to fetch suggestions", err);
    }
  }

  useEffect(() => {
    loadSuggestions(0, statusFilter);
    setPage(0);
  }, [statusFilter]);

  const handleStatusChange = (value: string | null) => {
    setStatusFilter(value);
  };

  const handlePrevPage = () => {
    if (page > 0) loadSuggestions(page - 1);
  };

  const handleNextPage = () => {
    if (page < totalPages - 1) loadSuggestions(page + 1);
  };

  const handleRowClick = async (id: number, status: string) => {
    if (status === "APPROVED") {
      navigate(`/practice/problem/${id}`);
      return;
    }

    try {
      const problem = await fetchFullProblemById(id);

      const problemInfo = {
        id: problem.id,
        name: problem.title,
        solutionLang: problem.solutionLanguage || "",
        timeLimit: problem.timeLimit,
        memoryLimit: problem.memoryLimit,
        rating: problem.rate || "",
        topics: problem.tags || [],
        solutionCode: problem.solutionCode || "",
      };
      localStorage.setItem("problem_info_draft", JSON.stringify(problemInfo));

      const problemStatement = {
        title: problem.title,
        statement: problem.statement,
        inputFormat: problem.inputFormat,
        outputFormat: problem.outputFormat,
        notes: problem.notes,
      };
      localStorage.setItem(
        "problem_statement_draft",
        JSON.stringify(problemStatement)
      );

      const testcasesDraft = (problem.visibleTestCases || []).map(
        (tc: any, index: number) => ({
          id: tc.id || `test_${Date.now()}_${index}`,
          input: tc.input,
          visible: tc.visible ?? true,
          actualOutput: tc.output,
        })
      );
      localStorage.setItem(
        "problem_testcases_draft",
        JSON.stringify(testcasesDraft)
      );

      navigate(`/add-problem/info`);
    } catch (err) {
      console.error("Failed to prepare problem draft:", err);
    }
  };

  return (
    <div className="flex flex-col h-[90vh] p-8 space-y-6">
      <div className="flex justify-end">
        <SingleSelectDropdown
          label="Filter by Status:"
          options={STATUS_OPTIONS}
          value={statusFilter}
          onChange={handleStatusChange}
          placeholder="All Status"
          width="w-64"
        />
      </div>

      <div className="flex-1 overflow-hidden rounded-xl border border-white/5 bg-sidebar/10 shadow-xl">
        <div className="h-full overflow-y-auto custom-scroll">
          <Board<SuggestionRowProps & { index: number }>
            gridCols="grid-cols-[0.5fr_2fr_1fr_3fr]"
            data={problems.map((p, index) => ({
              ...p,
              index: index + 1 + page * 20,
            }))}
            columns={["#", "Problem Name", "Status", "Rejection Note"]}
            renderRow={(problem) => (
              <SuggestionRow
                key={problem.id}
                id={problem.id}
                index={problem.index}
                name={problem.name}
                status={problem.status}
                rejectionNote={problem.rejectionNote}
                onRowClick={() => handleRowClick(problem.id, problem.status)}
              />
            )}
          />
        </div>
      </div>

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