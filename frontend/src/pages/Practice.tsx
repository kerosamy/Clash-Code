import { useEffect, useState } from "react";
import Board from "../components/common/Board";
import ProblemRow, { type ProblemRowProps } from "../components/common/ProblemRow";
import { fetchProblems } from "../services/problem/browse.service";
import { mapProblemDtoToProblemRow } from "../utils/mapProblemDtoToProblemRow";




export default function Practice() {

  const [problems, setProblems] = useState<ProblemRowProps[]>([]);

  async function loadProblems(page = 0) {
    try {
      const backendPage = await fetchProblems(page, 10);
      const mapped = backendPage.content.map(mapProblemDtoToProblemRow);
      setProblems(mapped);
    } catch (err) {
      console.error("Failed to fetch problems", err);
    }
  }

  useEffect(() => {
    loadProblems();
  }, []);
  
  const handleProblemClick = (problem: ProblemRowProps) => {
    console.log("Problem clicked:", problem);
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
