import { useEffect, useState } from "react";
import Board from "../components/common/Board";
import ProblemRow, { type ProblemRowProps } from "../components/common/ProblemRow";
import { fetchProblems, fetchFilteredProblems, searchProblemsByTitle } from "../services/ProblemService";
import { mapProblemDtoToProblemRow } from "../utils/mapProblemDtoToProblemRow";
import TagsMultiSelectDropdown from "../components/common/TagsMultiSelectDropDown";
import { TagsFrontendValues, mapFrontendTagsToEnum } from "../utils/mapTags";
import DifficultySelector from "../components/common/DifficultySelector";
import { DifficultyLevel } from "../enums/DifficultyLevel";
import SearchBar from "../components/common/SearchBar";
import React from "react";
import { useNavigate } from "react-router-dom";




export default function Practice() {

  const [problems, setProblems] = useState<ProblemRowProps[]>([]);
  const [searchQuery, setSearchQuery] = React.useState("");
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [minDifficulty, setMinDifficulty] = useState<number>(DifficultyLevel.MIN);
  const [maxDifficulty, setMaxDifficulty] = useState<number>(DifficultyLevel.HARD_MAX);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1); // total pages from backend
  const navigate = useNavigate();

  // load problems filtered or unfiltered
  async function loadProblems(pageToLoad = 0) {
    try {

      if (searchQuery.trim() !== "") {
        // search by title
        const backendPage = await searchProblemsByTitle(searchQuery, pageToLoad, 20);
        const mapped = backendPage.content.map(mapProblemDtoToProblemRow);
        setProblems(mapped);
        setPage(pageToLoad);
        setTotalPages(backendPage.totalPages);
        return;
      }
      
      const backendTags = mapFrontendTagsToEnum(selectedTags);
      const minRate = minDifficulty;
      const maxRate = maxDifficulty;
      

      const backendPage =
      backendTags.length > 0 ||
      minRate !== DifficultyLevel.MIN ||
      maxRate !== DifficultyLevel.HARD_MAX
        ? await fetchFilteredProblems(backendTags, minRate, maxRate, pageToLoad, 20)
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
  }, [selectedTags, minDifficulty, maxDifficulty, searchQuery]);  // re-run when filters/search triggered
  
const handleProblemClick = (problem: ProblemRowProps) => {
  console.log("Problem clicked:", problem);
   navigate(`/practice/problem/${problem.id}`);
};

  const handlePrevPage = () => {
    if (page > 0) loadProblems(page - 1);
  };

  const handleNextPage = () => {
    if (page < totalPages - 1) loadProblems(page + 1);
  };

  return (

      <div className="flex flex-col h-[90vh] space-y-4">
          <div className="flex items-center justify-between flex-wrap space-y-4">

            <SearchBar 
              value={searchQuery}
              onChange={setSearchQuery}
              placeholder="Search by problem name"
            />

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

          <div className="flex-1 overflow-y-auto custom-scroll">
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
