import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Board from "../../components/common/Board";
import { getRankName, getRankColor } from "../../utils/colorMapper";
import { getMatchHistory } from "../../services/MatchService";
import type { MatchHistoryDto } from "../../services/MatchService";
import { getUsername } from "../../utils/jwtDecoder";
import SingleSelectDropdown from "../../components/common/SingleSelectDropDown";
import LogoLoader from "../../components/Loader/LogoLoader";
import { formatDate, getStatusConfig } from "../../utils/matchUtils";

type TitleData = 
  | { type: 'FRIENDLY' }
  | { type: 'SAME'; name: string; color: string }
  | { type: 'CHANGE'; old: { name: string; color: string }; new: { name: string; color: string } };

const GRID_COLS = "grid-cols-[0.5fr_1.2fr_1fr_2.5fr_0.8fr_0.8fr_0.8fr_1.5fr]";
const COLUMNS = ["Id", "When", "Opponent", "Problem", "Status", "Change", "New Rate", "Title"];
const FILTER_OPTIONS = ["All Matches", "Rated Only", "Friendly Only"];

const getTitleData = (match: MatchHistoryDto): TitleData => {
  if (!match.rated || match.newRating == null) {
    return { type: 'FRIENDLY' };
  }

  const oldRating = (match.newRating || 0) - (match.rateChange || 0);
  const oldRankName = getRankName(oldRating);
  const newRankName = getRankName(match.newRating);
  const newRankColor = getRankColor(match.newRating);

  if (oldRankName === newRankName) {
    return { type: 'SAME', name: newRankName, color: newRankColor };
  }

  return {
    type: 'CHANGE',
    old: { name: oldRankName, color: getRankColor(oldRating) },
    new: { name: newRankName, color: newRankColor }
  };
};

const MatchRow = ({ match }: { match: MatchHistoryDto }) => {
  const { date, time } = formatDate(match.time);
  const status = getStatusConfig(match.rank);
  const titleData = getTitleData(match);
  
  const hasRateChange = match.rated && match.rateChange !== null;
  const hasNewRate = match.rated && match.newRating !== null;

  const cellStyle = "text-sm font-bold truncate flex items-center justify-center";

  return (
    <div 
      className={`grid ${GRID_COLS} gap-4 px-6 py-4 border-b border-sidebar hover:bg-white/5 transition-colors cursor-pointer`}
    >
      {/* 1. ID */}
      <div className={`${cellStyle}`}>
        <span className="text-white">
          #{match.matchId}
        </span>
      </div>
      
      {/* 2. When (Date & Time) */}
      <div className={`${cellStyle} flex-col items-start`}>
        <span className="text-white">{date}</span>
        <span className="text-white/50 text-xs">{time}</span>
      </div>

      {/* 3. Opponent */}
      <div className={`${cellStyle}`}>
        <span className="text-white truncate">
          {match.opponent}
        </span>
      </div>

      {/* 4. Problem */}
      <div className={`${cellStyle}`}>
        <span className="text-white truncate" title={match.problem}>
          {match.problem}
        </span>
      </div>

      {/* 5. Status */}
      <div className={`${cellStyle}`}>
        <span className={`${status.color} tracking-wider`}>
          {status.text}
        </span>
      </div>

      {/* 6. Change */}
      <div className={`${cellStyle}`}>
        {hasRateChange ? (
          <span className={match.rateChange! >= 0 ? "text-winGreen" : "text-loseRed"}>
            {match.rateChange! > 0 ? "+" : ""}{match.rateChange}
          </span>
        ) : (
          <span className="text-white">—</span>
        )}
      </div>

      {/* 7. New Rate */}
      <div className={`${cellStyle}`}>
        {hasNewRate ? (
          <span className="text-white">{match.newRating}</span>
        ) : (
          <span className="text-white">—</span>
        )}
      </div>

      {/* 8. Title */}
      <div className={`${cellStyle}`}>
        {titleData.type === 'FRIENDLY' && <span className="text-white">—</span>}
        
        {titleData.type === 'SAME' && (
          <span style={{ color: titleData.color }}>{titleData.name}</span>
        )}

        {titleData.type === 'CHANGE' && (
          <>
            <span style={{ color: titleData.old.color }}>{titleData.old.name}</span>
            <span className="text-white/40">→</span>
            <span style={{ color: titleData.new.color }}>{titleData.new.name}</span>
          </>
        )}
      </div>
    </div>
  );
};

export default function Matches() {
  const [history, setHistory] = useState<MatchHistoryDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [filterType, setFilterType] = useState<string | null>("All Matches");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const { username: paramUsername } = useParams<{ username?: string }>();
  const navigate = useNavigate();

  const handleFilterChange = (newFilter: string | null) => {
    setFilterType(newFilter);
    setPage(0); 
  };

  useEffect(() => {
    fetchData();
  }, [page, filterType]); 

  const fetchData = async () => {
    if(paramUsername != getUsername()){
        navigate('/not-found', { replace: true });
    }
    setLoading(true);
    try {
      const data = await getMatchHistory(page, 10, filterType);   
      setHistory(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      console.error("Failed to fetch history", err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LogoLoader loadingMessage="Loading Matches..." />;

  return (
    <div className="w-full h-full flex flex-col p-6 gap-6 font-anta text-white bg-background">
      <div className="grid grid-cols-3 items-end border-b border-white/10 pb-4 w-full">
        <div></div>
        <div className="flex flex-col items-center justify-center">
          <h1 className="text-3xl font-bold font-anta text-orange p-2 text-center whitespace-nowrap">
            {getUsername()}'s Matches
          </h1>
          <p className="text-text/60 font-anta px-2 text-center">
            Total Matches: <span className="text-white">{history.length}</span>
          </p>
        </div>

        <div className="flex justify-end w-full">
          <SingleSelectDropdown 
            label="Filter By:"
            options={FILTER_OPTIONS}
            value={filterType}
            onChange={handleFilterChange}
            width="w-52"
            placeholder="All Matches"
          />
        </div>
      </div>

      <div className="flex-1 w-full flex flex-col gap-4">
        <div className="shadow-2xl shadow-black/20 rounded-xl overflow-hidden w-full border border-white/5 bg-container">
          <Board<MatchHistoryDto>
            data={history}
            columns={COLUMNS}
            gridCols={GRID_COLS}
            onRowClick={(item) => console.log("Clicked:", item.matchId)}
            renderRow={(match) => (
              <MatchRow key={match.matchId} match={match} />
            )}
          />
        </div>

        {history.length === 0 && (
          <div className="flex-1 flex items-center justify-center text-white/30 italic">
            No matches found.
          </div>
        )}
      </div>

      {totalPages >= 1 && (
        <div className="flex justify-center gap-4 py-2">
          <button 
            onClick={() => setPage(p => Math.max(0, p - 1))} 
            disabled={page === 0} 
            className="px-6 py-2 bg-container border border-white/10 text-white rounded-button hover:bg-orange hover:border-orange disabled:opacity-30 transition-all font-anta text-sm"
          >
            Previous
          </button>
          <span className="flex items-center text-white/80 text-sm bg-container px-5 rounded-button border border-white/5 font-mono">
            {page + 1} / {totalPages}
          </span>
          <button 
            onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))} 
            disabled={page >= totalPages - 1} 
            className="px-6 py-2 bg-container border border-white/10 text-white rounded-button hover:bg-orange hover:border-orange disabled:opacity-30 transition-all font-anta text-sm"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}