import { useEffect, useState } from "react";
import Board from "../components/common/Board";
import ProblemRow, { type ProblemRowProps } from "../components/common/ProblemRow";
import { fetchProblems, fetchFilteredProblems } from "../services/problem.service";
import { mapProblemDtoToProblemRow } from "../utils/mapProblemDtoToProblemRow";
import TagsMultiSelectDropdown from "../components/common/TagsMultiSelectDropDown";
import { TagsFrontendValues, mapFrontendTagsToEnum } from "../utils/mapTags";
import DifficultySelector from "../components/common/DifficultySelector";
import { DifficultyLevel } from "../enums/DifficultyLevel";




export default function Practice() {

  const [problems, setProblems] = useState<ProblemRowProps[]>([]);
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [minDifficulty, setMinDifficulty] = useState<number>(DifficultyLevel.MIN);
  const [maxDifficulty, setMaxDifficulty] = useState<number>(DifficultyLevel.HARD_MAX);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1); // total pages from backend

  // load problems filtered or unfiltered
  async function loadProblems(pageToLoad = 0) {
    try {
      const backendTags = mapFrontendTagsToEnum(selectedTags);
      const rate = minDifficulty > DifficultyLevel.MIN ? minDifficulty : null;

      const backendPage =
      backendTags.length > 0 || rate
        ? await fetchFilteredProblems(backendTags, rate, pageToLoad, 20)
        : await fetchProblems(pageToLoad, 20);


      const mapped = backendPage.content.map(mapProblemDtoToProblemRow);
      setProblems(mapped);
      setPage(pageToLoad);
      setTotalPages(backendPage.totalPages);

    } catch (err) {
      console.error("Failed to fetch problems", err);
    }
  }

  useEffect(() => {
    loadProblems();
  }, [selectedTags, minDifficulty, maxDifficulty]);  // re-run when filters triggered
  
  const handleProblemClick = (problem: ProblemRowProps) => {
    console.log("Problem clicked:", problem);
  };

  const handlePrevPage = () => {
    if (page > 0) loadProblems(page - 1);
  };

  const handleNextPage = () => {
    if (page < totalPages - 1) loadProblems(page + 1);
  };

  return (

      <div className="space-y-6 custom-scroll overflow-y-auto">
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

          <div className="custom-scroll max-h-[80vh] overflow-y-auto">
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

          <div className="flex justify-center gap-4 mt-4">
            <button
              onClick={handlePrevPage}
              disabled={page === 0}
              className="px-4 py-2 bg-orange text-white rounded-button hover:bg-orange/90 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
            >
              {"<"} Previous
            </button>

            <span className="flex items-center text-white">
              Page {page + 1} of {totalPages}
            </span>

            <button
              onClick={handleNextPage}
              disabled={page >= totalPages - 1}
              className="px-4 py-2 bg-orange text-white rounded-button hover:bg-orange/90 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
            >
              Next {">"}
            </button>
          </div>


      </div>

  );
}
