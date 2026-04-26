import { RECOVERY_OPTIONS } from "../../utils/recoveryQuestions";

interface RecoveryQuestionModalProps {
  isOpen: boolean;
  onClose: () => void;
  recoveryQuestion: string;
  setRecoveryQuestion: (value: string) => void;
  recoveryAnswer: string;
  setRecoveryAnswer: (value: string) => void;
  errorQuestion?: string;
  errorAnswer?: string;
}

export default function RecoveryQuestionModal({
  isOpen,
  onClose,
  recoveryQuestion,
  setRecoveryQuestion,
  recoveryAnswer,
  setRecoveryAnswer,
  errorQuestion,
  errorAnswer,
}: RecoveryQuestionModalProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/70 z-50">
      <div className="bg-container p-6 rounded-button shadow-lg w-full max-w-md text-white">
        <h2 className="text-lg mb-4">Recovery Question</h2>

        <select
          value={recoveryQuestion}
          onChange={(e) => setRecoveryQuestion(e.target.value)}
          className="w-full p-3 rounded-button bg-background text-white border border-gray-600 focus:outline-none focus:border-orange"
        >
          <option value="">Select recovery question</option>
          
          {RECOVERY_OPTIONS.map((option) => (
            <option 
              key={option.value} 
              value={option.value}
            >
              {option.label}
            </option>
          ))}
        </select>
        
        {errorQuestion && <p className="text-red-500 text-sm mt-1">{errorQuestion}</p>}

        <input
          type="text"
          placeholder="Recovery Answer"
          value={recoveryAnswer}
          onChange={(e) => setRecoveryAnswer(e.target.value)}
          className="w-full p-3 mt-3 rounded-button bg-background text-white border border-gray-600 focus:outline-none focus:border-orange"
        />
        {errorAnswer && <p className="text-red-500 text-sm mt-1">{errorAnswer}</p>}

        <div className="flex justify-end gap-3 mt-4">
          <button
            type="button"
            className="bg-background border border-gray-600 p-2 rounded-button text-gray-300 hover:text-white"
            onClick={onClose}
          >
            Cancel
          </button>
          <button
            type="button"
            className="bg-orange p-2 rounded-button hover:opacity-90 text-white"
            onClick={onClose}
          >
            Save
          </button>
        </div>
      </div>
    </div>
  );
}