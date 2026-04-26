import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Board from "../components/common/Board";
import LeaderboardRow from "../components/common/LeaderboardRow";
import { getLeaderboard, type LeaderboardUserDto } from "../services/UserService";
import { getRankName, getRankColor } from "../utils/colorMapper";
import LogoLoader from "../components/Loader/LogoLoader";
import { waitForLoader } from "../components/Loader/WaitLoader";

export default function LeaderBoard() {
  const [data, setData] = useState<LeaderboardUserDto[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const PAGE_SIZE = 20;

  async function loadLeaderboard(pageToLoad = 0) {
    setLoading(true);
    const startTime = Date.now();
    try {

      const response = await getLeaderboard(pageToLoad, PAGE_SIZE);
      await waitForLoader(startTime);

      setData(response.content);
      setTotalPages(response.totalPages);
      setPage(pageToLoad);
    } catch (error) {
      console.error("Failed to load leaderboard", error);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadLeaderboard();
  }, []);


    if (loading) {
    return(
      <div className="flex flex-col h-screen font-anta">
        <LogoLoader loadingMessage="Loading Problems" />
      </div>
    );
  }

  return (
    <div className="flex flex-col h-[90vh] space-y-6 p-6 max-w-6xl mx-auto w-full">
      {/* Simplified Header - Halo Removed */}
      <div className="text-center py-4">
        <h1 className="text-4xl font-anta text-white uppercase tracking-[0.25em]">
          Hall of <span className="text-orange">Fame</span>
        </h1>
        <p className="text-text/30 font-anta text-[10px] uppercase mt-1 tracking-[0.4em]">
          Global Rankings
        </p>
      </div>

      {/* Main Board Container */}
      <div className="flex-1 overflow-hidden flex flex-col bg-sidebar/20 backdrop-blur-sm rounded-xl border border-white/5">
        <div className="flex-1 overflow-y-auto custom-scroll">
          <Board<LeaderboardUserDto>
            data={data}
            columns={["POS", "USER", "TIER", "ELO"]}
            gridCols="grid-cols-[80px_1fr_1fr_120px]"
            renderRow={(user) => (
              <LeaderboardRow
                key={user.username}
                position={page * PAGE_SIZE + data.indexOf(user) + 1}
                username={user.username}
                rank={getRankName(user.currentRate)}
                rankColor={getRankColor(user.currentRate)}
                rate={user.currentRate}
                onUsernameClick={() => navigate(`/profile/${user.username}/overview`)}
              />
            )}
          />
        </div>
      </div>

      {/* Pagination */}
      <div className="flex justify-between items-center px-4 py-2">
        <button
          onClick={() => loadLeaderboard(page - 1)}
          disabled={page === 0 || loading}
          className="font-anta text-xs text-text/40 hover:text-orange disabled:opacity-10 transition-colors uppercase tracking-widest"
        >
          Previous
        </button>

        <div className="flex items-center gap-4">
            <div className="h-[1px] w-8 bg-white/10" />
            <span className="font-anta text-xs text-orange tracking-widest">
                {page + 1} / {totalPages}
            </span>
            <div className="h-[1px] w-8 bg-white/10" />
        </div>

        <button
          onClick={() => loadLeaderboard(page + 1)}
          disabled={page >= totalPages - 1 || loading}
          className="font-anta text-xs text-text/40 hover:text-orange disabled:opacity-10 transition-colors uppercase tracking-widest"
        >
          Next
        </button>
      </div>
    </div>
  );
}