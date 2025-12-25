import  { useState } from "react";

interface RejectionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (note: string) => void;
}

export default function RejectionModal({ isOpen, onClose, onSubmit }: RejectionModalProps) {
  const [note, setNote] = useState("");

  if (!isOpen) return null;

  const handleSubmit = () => {
    if (!note.trim()) return;
    onSubmit(note.trim());
    setNote("");
  };

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/50 z-50">
      <div className="bg-sidebar/90 rounded-xl p-6 w-96 shadow-xl border border-white/10">
        <h2 className="text-lg font-anta text-white mb-4">Enter Rejection Note</h2>
        <textarea
          className="w-full h-24 p-2 rounded-md border border-white/20 bg-sidebar/20 text-white resize-none focus:outline-none focus:ring-2 focus:ring-orange"
          value={note}
          onChange={(e) => setNote(e.target.value)}
          placeholder="Write your rejection note here..."
        />
        <div className="flex justify-end gap-3 mt-4">
          <button
            className="px-4 py-2 bg-gray-700 text-white rounded-xl hover:bg-gray-600 transition"
            onClick={onClose}
          >
            Cancel
          </button>
          <button
            className="px-4 py-2 bg-orange text-white rounded-xl hover:bg-orange/90 transition"
            onClick={handleSubmit}
          >
            Submit
          </button>
        </div>
      </div>
    </div>
  );
}
