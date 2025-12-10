import Board from '../../components/common/Board';

interface Submission {
  id: string;
  time: string;
  verdict: string;
  isCorrect: boolean;
}

interface Player {
  username: string;
  avatarUrl: string;
  rankColor: string; 
  submissions: Submission[];
}

const mockPlayer1: Player = {
  username: "Kero_Samy_20",
  avatarUrl: "https://i.pravatar.cc/300?u=kero", 
  rankColor: "text-diamond",
  submissions: [
    { id: '1', time: "04 : 40", verdict: "Wrong Answer on test 3", isCorrect: false },
    { id: '2', time: "04 : 40", verdict: "Wrong Answer on test 3", isCorrect: false },
  ]
};

const mockPlayer2: Player = {
  username: "John_willy_30",
  avatarUrl: "https://i.pravatar.cc/300?u=john", 
  rankColor: "text-master",
  submissions: [
    { id: '1', time: "04 : 40", verdict: "Wrong Answer on test 3", isCorrect: false },
    { id: '2', time: "04 : 40", verdict: "Wrong Answer on test 3", isCorrect: false },
  ]
};


const PlayerHeader = ({ player }: { player: Player }) => (
  <div className="flex flex-col items-center gap-3"> 
    <div className="relative">
      <img 
        src={player.avatarUrl} 
        alt={player.username} 
        className="w-48 h-48 rounded-full object-cover border-4 border-container shadow-xl"
      />
    </div>
    
    <h2 className={`text-3xl font-anta font-bold tracking-wide ${player.rankColor}`}>
      {player.username}
    </h2>
  </div>
);

const VersusBadge = () => (
  <div className="flex flex-col items-center justify-center mx-2 md:mx-8 select-none">
     <div className="font-black italic flex items-end leading-none">
        <span className="text-blue-600 text-[8rem] transform -translate-y-2 -translate-x-2 drop-shadow-lg">V</span>
        <span className="text-loseRed text-[8rem] transform translate-y-2 translate-x-2 drop-shadow-lg">S</span>
     </div>
  </div>
);


export default function MatchState() {
  const columns = ["Time", "Verdict"];
  const gridConfig = "grid-cols-[1fr_2fr]"; 

  const renderSubmissionRow = (item: Submission) => (
    <div className={`grid ${gridConfig} gap-4 px-6 py-4 border-b border-sidebar items-center hover:bg-white/5 transition-colors`}>
      <span className="text-white font-anta text-center text-lg tracking-wider">
        {item.time}
      </span>
      <span className={`font-anta text-center text-lg ${item.isCorrect ? 'text-winGreen' : 'text-loseRed'}`}>
        {item.verdict}
      </span>
    </div>
  );

  return (
    <div className="w-full min-h-full flex flex-col gap-2 p-6"> 
      
      <div className="flex flex-col md:flex-row items-center justify-center">
        <PlayerHeader player={mockPlayer1} />
        <VersusBadge />
        <PlayerHeader player={mockPlayer2} />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-12 w-full max-w-[1400px] mx-auto">
        <Board<Submission>
          data={mockPlayer1.submissions}
          columns={columns}
          gridCols={gridConfig}
          renderRow={renderSubmissionRow}
        />

        <Board<Submission>
          data={mockPlayer2.submissions}
          columns={columns}
          gridCols={gridConfig}
          renderRow={renderSubmissionRow}
        />
      </div>

    </div>
  );
}