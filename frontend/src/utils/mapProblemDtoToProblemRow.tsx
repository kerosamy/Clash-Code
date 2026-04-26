import { Status } from "../enums/Status";
import { type ProblemRowProps } from "../components/common/ProblemRow";
import type { ProblemTags } from "../enums/ProblemTags";
import { EnumToFrontendTagMap } from "./mapTags";

export function mapProblemDtoToProblemRow(problem: any): ProblemRowProps {
  return {
    id: problem.id,
    name: problem.title,
    tags: problem.tags.map((tag: ProblemTags) =>
      EnumToFrontendTagMap[tag] ?? tag
    ),
    difficulty: problem.rate,
    solvers: problem.submissionsCount,
    status: Status.Unsolved, 
  };
}
