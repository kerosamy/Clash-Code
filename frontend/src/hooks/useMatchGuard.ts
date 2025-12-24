import { useEffect, useState } from 'react';
import { useNavigate, useBlocker } from 'react-router-dom';

interface UseMatchGuardOptions {
  matchId: string;
  onResign: () => Promise<void>;
  enabled?: boolean;
}

interface UseMatchGuardReturn {
  showResignModal: boolean;
  setShowResignModal: (show: boolean) => void;
  handleResignConfirm: () => Promise<void>;
  handleResignCancel: () => void;
}

export const useMatchGuard = ({
  matchId,
  onResign,
  enabled = true
}: UseMatchGuardOptions): UseMatchGuardReturn => {
  const navigate = useNavigate();
  const [showResignModal, setShowResignModal] = useState(false);
  const [pendingNavigation, setPendingNavigation] = useState<string | null>(null);
  const [isResigning, setIsResigning] = useState(false);

  const blocker = useBlocker(
    ({ currentLocation, nextLocation }) => {
      if (!enabled || isResigning) return false;
      
      const isLeavingMatch = 
        currentLocation.pathname !== nextLocation.pathname &&
        !nextLocation.pathname.startsWith(`/play-game/${matchId}`);
      
      return isLeavingMatch;
    }
  );

  useEffect(() => {
    if (blocker.state === 'blocked') {
      setShowResignModal(true);
      setPendingNavigation(blocker.location?.pathname || null);
    }
  }, [blocker.state]);

  useEffect(() => {
    if (!enabled) return;

    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      e.preventDefault();
      e.returnValue = 'You have an active match. Are you sure you want to leave?';
      return e.returnValue;
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => window.removeEventListener('beforeunload', handleBeforeUnload);
  }, [enabled]);

  const handleResignConfirm = async () => {
    try {
      setIsResigning(true);
      await onResign();
      
      setShowResignModal(false);
      
      if (blocker.state === 'blocked' && blocker.proceed) {
        blocker.proceed();
      } else if (pendingNavigation) {
        navigate(pendingNavigation);
      }
    } catch (error) {
      console.error('Failed to resign from match:', error);
      alert('Failed to resign from match. Please try again.');
    } finally {
      setIsResigning(false);
      setPendingNavigation(null);
    }
  };

  const handleResignCancel = () => {
    setShowResignModal(false);
    setPendingNavigation(null);
    
    if (blocker.state === 'blocked' && blocker.reset) {
      blocker.reset();
    }
  };

  return {
    showResignModal,
    setShowResignModal,
    handleResignConfirm,
    handleResignCancel
  };
};