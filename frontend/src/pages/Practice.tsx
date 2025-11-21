import Board from "../components/common/Board";
import ProblemRow, { type ProblemRowProps } from "../components/common/ProblemRow";

const SAMPLE_PROBLEMS: ProblemRowProps[] = [
  {
    id: 9099090990900991,
    name: "Sample Problem",
    tags: ["DP", "Two Pointers"],
    difficulty: 200,
    solvers: 19090909909099999,
    status: "solved",
  },
  {
    id: 2,
    name: "Sample Problem",
    tags: ["Greedy"],
    difficulty: 2000,
    solvers: 10000000,
    status: "unsolved",
  },
];


export default function Practice() {
  const handleProblemClick = (problem: ProblemRowProps) => {
    console.log("Problem clicked:", problem);
  };

  return (
    <div className="space-y-6">
      <Board<ProblemRowProps>
        data={SAMPLE_PROBLEMS}
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
