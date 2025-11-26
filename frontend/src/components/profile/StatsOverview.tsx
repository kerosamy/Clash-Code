interface UserStats {
  solvedProblems: number;
  attemptedProblems: number;
  matchesPlayed: number;
  matchesWon: number;
}

interface StatsOverviewProps {
  stats: UserStats;
  color: string;
}

export default function StatsOverview({ stats, color }: StatsOverviewProps) {
  return (
    <div className="bg-container rounded-lg p-8 items-center">
      <div className="space-y-4 text-text">
        <div className="flex items-center gap-2">
          <span className="text-text text-xl">Solved Problems:</span>
          <span className="text-2xl font-semibold" style={{ color }}>{stats.solvedProblems}</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-text text-xl">Attempted Problems:</span>
          <span className="text-2xl font-semibold" style={{ color }}>{stats.attemptedProblems}</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-text text-xl">Matches Played:</span>
          <span className="text-2xl font-semibold" style={{ color }}>{stats.matchesPlayed}</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-text text-xl">Matches Won:</span>
          <span className="text-2xl font-semibold" style={{ color }}>{stats.matchesWon}</span>
        </div>
      </div>
    </div>
  );
}