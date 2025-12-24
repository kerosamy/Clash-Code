import { useState } from "react";
import { useNavigate } from "react-router-dom";
import ConfirmationModal from "../components/common/ConfirmationModal";
import { clearToken } from "../utils/jwtDecoder";

export default function LogoutPage() {
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(true);

  const handleConfirm = () => {
    clearToken();
    navigate("/log-in", { replace: true });
  };

  const handleClose = () => {
    navigate(-1); // go back if user cancels
  };

  return (
    <ConfirmationModal
      isOpen={isOpen}
      onClose={handleClose}
      onConfirm={handleConfirm}
      title="Confirm Logout"
      message="Are you sure you want to log out?"
      confirmText="Logout"
      cancelText="Cancel"
    />
  );
}
