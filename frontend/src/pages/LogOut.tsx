import { useState } from "react";
import { useNavigate } from "react-router-dom";
import ConfirmationModal from "../components/common/ConfirmationModal";
import { clearToken } from "../utils/jwtDecoder";
import { clearActiveMatch, hasActiveMatch } from "../utils/matchState";

export default function LogoutPage() {
  const navigate = useNavigate();
  const [isOpen] = useState(true);
  const activeMatch = hasActiveMatch();

  const handleConfirm = () => {
    clearToken();
    clearActiveMatch();
    sessionStorage.clear();
    navigate("/sign-up", { replace: true });
  };

  const handleClose = () => {
    navigate(-1);
  };

  return (
    <ConfirmationModal
      isOpen={isOpen}
      onClose={handleClose}
      onConfirm={handleConfirm}
      title="Confirm Logout"
      message={
        activeMatch
          ? "You have an active match. Logging out will resign you from the match. Are you sure?"
          : "Are you sure you want to log out?"
      }
      confirmText="Logout"
      cancelText="Cancel"
    />
  );
}