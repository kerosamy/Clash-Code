import { DifficultyLevel } from "../../enums/DifficultyLevel";

interface DifficultySelectorProps {
  min: number;
  max: number;
  onMinChange: (value: number) => void;
  onMaxChange: (value: number) => void;
}

export default function DifficultySelector({
  min,
  max,
  onMinChange,
  onMaxChange,
}: DifficultySelectorProps) {

  const clamp = (value: number) => {
    if (!value) return DifficultyLevel.MIN;

    let v = value;
    if (v < DifficultyLevel.MIN) v = DifficultyLevel.MIN;
    if (v > DifficultyLevel.HARD_MAX) v = DifficultyLevel.HARD_MAX;
    return v;
  };

  const handleKeyPress = (
    e: React.KeyboardEvent<HTMLInputElement>,
    callback: (value: number) => void
  ) => {
    if (e.key === "Enter") {
      callback(clamp(Number(e.currentTarget.value)));
      e.currentTarget.blur();
    }
  };

  return (
    <div className="flex items-center gap-4">
      <span className="text-text font-anta text-sm whitespace-nowrap">
        Pick Difficulty
      </span>

      <div className="flex items-center gap-1">

        <input
          type="number"
          value={min}
          onChange={(e) => onMinChange(Number(e.target.value))}
          onBlur={(e) => onMinChange(clamp(Number(e.target.value)))}
          onKeyDown={(e) => handleKeyPress(e, onMinChange)}
          placeholder="min"
          className="bg-container text-text placeholder-text/50 px-3 py-2 rounded-xl border border-sidebar/30 focus:outline-none focus:border-orange/50 transition-colors font-anta text-sm w-24 text-center"
        />

        <div className="w-16 h-1 bg-gradient-to-r from-orange/30 to-orange rounded-full" />

        <input
          type="number"
          value={max}
          onChange={(e) => onMaxChange(Number(e.target.value))}
          onBlur={(e) => onMaxChange(clamp(Number(e.target.value)))}
          onKeyDown={(e) => handleKeyPress(e, onMaxChange)}
          placeholder="max"
          className="bg-container text-text placeholder-text/50 px-3 py-2 rounded-xl border border-sidebar/30 focus:outline-none focus:border-orange/50 transition-colors font-anta text-sm w-24 text-center"
        />
      </div>
    </div>
  );
}
