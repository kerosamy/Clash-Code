import { getStatusColor } from "../../utils/getStatusColor";
import { getDifficultyColor } from "../../utils/getDifficultyColor";
import { Status } from "../../enums/Status";

export interface ProblemRowProps {
    id: number;
    name: string;
    tags: string[];
    difficulty: number;
    solvers: number;
    status?: Status;
    onClick?: () => void;
    className?: string;
}
  
  
export default function ProblemRow({
    id,
    name,
    tags,
    difficulty,
    solvers,
    status = Status.Unsolved,
    onClick,
    className = "",
  }: ProblemRowProps) {

    const statusColor = getStatusColor(status);
    const difficultyColor = getDifficultyColor(difficulty);
  
    return (
      <div
        onClick={onClick}
        className={`grid grid-cols-[80px_1fr_2fr_100px_100px_60px] gap-4 px-6 py-3 items-center hover:bg-sidebar/20 transition-colors ${className}`}
      >
        <span className="text-text text-center font-anta text-sm truncate">{id}</span>
        <span className="text-text text-center font-anta text-sm truncate">{name}</span>
        <div className="flex flex-wrap gap-1">
          {tags.map((tag) => (
            <span
              key={tag}
              className="text-xs bg-sidebar/50 text-text/80 px-2 py-0.5 rounded-md truncate"
            >
              {tag}
            </span>
          ))}
        </div>
        <span className={`text-center font-anta text-sm ${difficultyColor}`}>
          {difficulty}
        </span>
        <span className="text-center font-anta text-sm text-text truncate">{solvers}</span>
        <span className={`text-center font-anta text-sm ${statusColor}`}>
          {status === "solved" ? "✓" : status === "attempted" ? "~" : "-"}
        </span>
      </div>
    );
}
  