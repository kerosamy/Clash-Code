import { useEffect, useState } from "react";
import Board from "../components/common/Board";
import ProblemRow, { type ProblemRowProps } from "../components/common/ProblemRow";
import { fetchProblems } from "../services/problem.service";
import { mapProblemDtoToProblemRow } from "../utils/mapProblemDtoToProblemRow";
import { useNavigate } from "react-router-dom";



export default function Practice() {

  const [problems, setProblems] = useState<ProblemRowProps[]>([]);
  const [loadParams, setLoadParams] = useState({ query: '', category: '' });
  const navigate = useNavigate();

  async function loadProblems(page = 0, filters = {}) {
    try {
      const backendPage = await fetchProblems(page, 10);
      const mapped = backendPage.content.map(mapProblemDtoToProblemRow);
      setProblems(prev => page === 0 ? mapped : [...prev, ...mapped]);
    } catch (err) {
      console.error("Failed to fetch problems", err);
    }
  }

  useEffect(() => {
    loadProblems();
  }, [loadParams]);  // re-run when loadParams change supposting filtering in future
  
  const handleProblemClick = (problem: ProblemRowProps) => {
    console.log("Problem clicked:", problem);
    navigate(`/problem/${problem.id}`);
  };

  return (
    <div className="space-y-6">
      <Board<ProblemRowProps>
        data={problems}
        columns={["#", "Name", "Tags", "Diff", "#Solvers", "Stat"]}
        onRowClick={handleProblemClick}
        renderRow={(problem, onClick) => (
          <ProblemRow
            key={problem.id}
            {...problem}
            onClick={onClick}
            className="cursor-pointer"
          />
        )}
      />
    </div>
  );
}
