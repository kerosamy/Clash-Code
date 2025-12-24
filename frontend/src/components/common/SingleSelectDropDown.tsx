import { ChevronDown } from "lucide-react";
import { useState, useRef, useEffect } from "react";

interface SingleSelectDropdownProps {
  label: string;
  options: string[];
  value: string | null;
  onChange: (value: string | null) => void;
  width?: string;
  placeholder?: string;
}

export default function SingleSelectDropdown({
  label,
  options,
  value,
  onChange,
  width = "w-52",
  placeholder = "Select an option",
}: SingleSelectDropdownProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [search, setSearch] = useState("");
  const dropdownRef = useRef<HTMLDivElement>(null);

  const filteredOptions = options.filter((option) =>
    option.toLowerCase().includes(search.toLowerCase())
  );

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
        setSearch("");
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const selectItem = (option: string) => {
    onChange(option);
    setIsOpen(false);
    setSearch("");
  };

  const clearSelection = () => {
    onChange(null);
  };

  const handleEnter = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key !== "Enter") return;

    if (filteredOptions.length === 0) return;

    selectItem(filteredOptions[0]);
  };

  return (
    <div className="flex items-center gap-4" ref={dropdownRef}>
      <span className="text-text font-anta text-sm whitespace-nowrap">{label}</span>

      <div className="relative">
        <button
          onClick={() => setIsOpen(!isOpen)}
          className={`${width} bg-container text-text/50 px-4 py-3 rounded-xl border border-sidebar/30 
      hover:border-orange/50 transition-colors font-anta flex items-center gap-2 justify-between truncate`}
        >
          <span className="text-sm truncate">
            {value || placeholder}
          </span>

          <div className="flex items-center gap-1">
            {value && (
              <button
                type="button"
                aria-label="Clear selection"

                onClick={(e) => {
                  e.stopPropagation();
                  clearSelection();
                }}

                className="flex items-center justify-center w-6 h-6 rounded-full 
                      text-red-500 hover:bg-red-500/10 hover:text-red-600 
                      transition-all duration-200"
              >
                <span className="text-xl leading-none font-bold">×</span>
              </button>
            )}

            <ChevronDown
              className={`w-4 h-4 transition-transform duration-300 ${isOpen ? "rotate-180" : ""}`}
            />
          </div>
        </button>

        {isOpen && (
          <div
            className={`${width} absolute top-full mt-2 bg-sidebar border border-sidebar/40 
            rounded-xl z-50 shadow-lg max-h-64 overflow-y-auto custom-scroll`}
          >
            <div className="px-3 py-2 bg-sidebar border-b border-container">
              <input
                className="w-full bg-sidebar text-text placeholder-text/40 px-2 py-1 rounded-md font-anta text-sm"
                placeholder="Type to filter"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                onKeyDown={handleEnter}
              />
            </div>

            {filteredOptions.map((option) => {
              const selected = value === option;
              return (
                <button
                  key={option}
                  onClick={() => selectItem(option)}
                  className={`w-full text-left px-3 py-2 transition-colors font-anta text-sm flex items-center justify-between truncate
                    ${selected ? "bg-orange/20 text-orange" : "text-text hover:bg-gray-800"}
                  `}
                >
                  <span className="truncate">{option}</span>
                  {selected && <span className="text-orange font-bold">✓</span>}
                </button>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}