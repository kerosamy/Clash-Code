import { type ReactNode, useEffect, useState } from 'react';
import { useNavigate, useLocation, useBlocker } from 'react-router-dom';
import { resignMatch } from '../services/MatchService';

interface MatchNavigationGuardProps {
  children: ReactNode;
}

const MatchNavigationGuard = ({ children }: MatchNavigationGuardProps) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [showResignModal, setShowResignModal] = useState(false);
  const [pendingNavigation, setPendingNavigation] = useState<string | null>(null);

  const isInMatch = () => {
    const matchId = sessionStorage.getItem('currentMatchId');
    const isMatchRoute = location.pathname.includes('/play-game/');
    return matchId && isMatchRoute;
  };

  const shouldBlockNavigation = (nextLocation: any): boolean => {
    const matchId = sessionStorage.getItem('currentMatchId');
    const isCurrentlyInMatch = location.pathname.includes('/play-game/');
    const isNavigatingToMatch = nextLocation.location.pathname.includes('/play-game/');

    return Boolean(matchId && isCurrentlyInMatch && !isNavigatingToMatch);
  };

  const blocker = useBlocker(shouldBlockNavigation);

  useEffect(() => {
    if (blocker.state === 'blocked') {
      setPendingNavigation(blocker.location.pathname);
      setShowResignModal(true);
    }
  }, [blocker]);

  const handleResign = async () => {
    try {
      const matchId = sessionStorage.getItem('currentMatchId');
      
      await resignMatch(Number(matchId));

      sessionStorage.removeItem('currentMatchId');

      setShowResignModal(false);

      if (blocker.state === 'blocked') {
        blocker.proceed();
      } else if (pendingNavigation) {
        navigate(pendingNavigation);
      }
      
      setPendingNavigation(null);
    } catch (error) {
      console.error('Failed to resign from match:', error);
      alert('Failed to resign from match. Please try again.');
    }
  };

  const handleCancel = () => {
    setShowResignModal(false);
    setPendingNavigation(null);
    if (blocker.state === 'blocked') {
      blocker.reset();
    }
  };

  useEffect(() => {
    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      if (isInMatch()) {
        e.preventDefault();
        e.returnValue = 'You are currently in a match. Are you sure you want to leave? You will need to resign first.';
      }
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => window.removeEventListener('beforeunload', handleBeforeUnload);
  }, [location]);

  return (
    <>
      {children}
      
      {showResignModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-8 max-w-md w-full mx-4 shadow-2xl">
            <div className="text-center">
              <div className="mb-4">
                <svg
                  className="mx-auto h-12 w-12 text-yellow-500"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                  />
                </svg>
              </div>
              <h2 className="text-2xl font-bold text-gray-900 mb-2">
                Match in Progress
              </h2>
              <p className="text-gray-600 mb-6">
                You are currently in an active match. You must resign from the match before navigating away.
              </p>
              <div className="flex flex-col gap-3">
                <button
                  onClick={handleResign}
                  className="w-full bg-red-600 hover:bg-red-700 text-white font-semibold py-3 px-4 rounded-lg transition duration-200"
                >
                  Resign and Leave
                </button>
                <button
                  onClick={handleCancel}
                  className="w-full bg-gray-600 hover:bg-gray-700 text-white font-semibold py-3 px-4 rounded-lg transition duration-200"
                >
                  Stay in Match
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default MatchNavigationGuard;