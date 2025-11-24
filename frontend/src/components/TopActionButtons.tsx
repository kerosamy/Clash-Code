import type { FC, ReactNode } from "react";

interface ActionButton {
  icon: ReactNode;
  label: string;
  value: string; // unique identifier for each button
  onClick?: () => void;
}

interface TopActionButtonsProps {
  buttons: ActionButton[];
  width?: string; // optional fixed width, e.g., "600px"
  selectedValue?: string; // value of the selected button
}

const TopActionButtons: FC<TopActionButtonsProps> = ({ 
  buttons, 
  width = "100%",
  selectedValue 
}) => {
  return (
    <div
      className="flex justify-evenly text-lg mb-6 mx-auto"
      style={{ width }}
    >
      {buttons.map((btn, idx) => (
        <button
          key={idx}
          onClick={btn.onClick}
          className={`
            flex items-center justify-center gap-2
            px-6 py-3
            rounded-full
            transition
            font-anta text-white
            whitespace-nowrap
            ${selectedValue === btn.value 
              ? 'bg-container' 
              : 'bg-transparent hover:bg-container/70'
            }
          `}
        >
          {btn.icon}
          <span>{btn.label}</span>
        </button>
      ))}
    </div>
  );
};

export default TopActionButtons;