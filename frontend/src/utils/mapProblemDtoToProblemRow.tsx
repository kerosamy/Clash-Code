import { Status } from "../enums/Status";
import { type ProblemRowProps } from "../components/common/ProblemRow";

export function mapProblemDtoToProblemRow(problem: any): ProblemRowProps {
  return {
    id: problem.id,
    name: problem.title,
    tags: problem.tags,
    difficulty: problem.rate,
    solvers: problem.submissionsCount,
    status: Status.Unsolved, 
  };
}
