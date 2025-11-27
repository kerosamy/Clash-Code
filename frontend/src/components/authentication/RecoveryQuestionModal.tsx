
// STEP 1: CHANGE THIS ARRAY
// We separate the 'value' (sent to backend) from the 'label' (shown to user)
const RECOVERY_OPTIONS = [
  { value: "MOTHERS_FRIEND", label: "What is your mother's friend name?" },
  { value: "FIRST_PET", label: "What was the name of your first pet?" },
  { value: "FIRST_CITY", label: "What was the first city you lived in?" },
  { value: "FAVORITE_MOVIE", label: "What is your favorite movie?" },
];

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
          
          {/* STEP 2: UPDATE THE MAP FUNCTION */}
          {RECOVERY_OPTIONS.map((option) => (
            <option 
              key={option.value} 
              value={option.value} // This sends "MOTHERS_FRIEND" to state/backend
            >
              {option.label}       {/* This shows "What is your mother..." to user */}
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