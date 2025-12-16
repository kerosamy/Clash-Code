interface ConfirmationModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  isLoading?: boolean;
}

export default function ConfirmationModal({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = "Confirm",
  cancelText = "Cancel",
  isLoading = false,
}: ConfirmationModalProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="w-full max-w-md bg-container border border-sidebar shadow-2xl rounded-button p-6 transform transition-all scale-100">
        <h3 className="text-2xl font-bold text-white font-anta mb-2">
          {title}
        </h3>

        <p className="text-text font-anta text-lg mb-8 leading-relaxed opacity-90">
          {message}
        </p>

        <div className="flex gap-4 justify-end">
          <button
            onClick={onClose}
            disabled={isLoading}
            className="px-6 py-2 rounded-button font-anta text-text hover:text-white border border-transparent hover:border-text/20 transition-all"
          >
            {cancelText}
          </button>

          <button
            onClick={onConfirm}
            disabled={isLoading}
            className="bg-orange hover:bg-orange/90 text-white px-8 py-2 rounded-button font-anta shadow-lg transition-all disabled:opacity-50"
          >
            {isLoading ? "Processing..." : confirmText}
          </button>
        </div>

      </div>
    </div>
  );
}