import { useEffect, useState } from "react";
import Board from "../components/common/Board";
import ProblemRow, { type ProblemRowProps } from "../components/common/ProblemRow";
import { fetchProblems } from "../services/problem.service";
import { mapProblemDtoToProblemRow } from "../utils/mapProblemDtoToProblemRow";
import TagsMultiSelectDropdown from "../components/common/TagsMultiSelectDropDown";
import { TagsFrontendValues } from "../utils/mapTags";
import DifficultySelector from "../components/common/DifficultySelector";
import { DifficultyLevel } from "../enums/DifficultyLevel";




export default function Practice() {

  const [problems, setProblems] = useState<ProblemRowProps[]>([]);
  const [loadParams, setLoadParams] = useState({ query: '', category: '' });
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [minDifficulty, setMinDifficulty] = useState<number>(DifficultyLevel.MIN);
  const [maxDifficulty, setMaxDifficulty] = useState<number>(DifficultyLevel.HARD_MAX);

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
  };

  return (
    <div className="space-y-6">
        <div className="flex items-center justify-between flex-wrap">

          <TagsMultiSelectDropdown
            label="Choose Problem Tags"
            options={TagsFrontendValues}
            value={selectedTags}        
            onChange={setSelectedTags}   
          />

          <DifficultySelector
            min={minDifficulty}
            max={maxDifficulty}
            onMinChange={setMinDifficulty}
            onMaxChange={setMaxDifficulty}
          />

        </div>

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
