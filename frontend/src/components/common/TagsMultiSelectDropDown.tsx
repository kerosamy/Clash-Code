import { ChevronDown } from "lucide-react";
import { useState, useRef, useEffect } from "react";

interface TagsMultiSelectDropdownProps {
  label: string;
  options: string[];
  value: string[];
  onChange: (values: string[]) => void;
  width?: string;
}

export default function TagsMultiSelectDropdown({
  label,
  options,
  value,
  onChange,
  width = "w-52",
}: TagsMultiSelectDropdownProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [search, setSearch] = useState("");
  const dropdownRef = useRef<HTMLDivElement>(null);


  const reorderedOptions = [
    ...value.filter((v) => options.includes(v)),
    ...options.filter((o) => !value.includes(o)), 
  ];


  const filteredOptions = reorderedOptions.filter((option) =>
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

  const toggleItem = (option: string) => {
    if (value.includes(option)) {
      onChange(value.filter((val) => val !== option));
    } else {
      onChange([option, ...value]); 
    }
  };

  const handleEnter = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key !== "Enter") return;

    if (filteredOptions.length === 0) return;

    const firstOption = filteredOptions[0];
    if (!value.includes(firstOption)) {
      onChange([firstOption, ...value]);
    }

    setSearch("");
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
            {value.length > 0 ? value.join(", ") : "Select tags"}
          </span>
          <ChevronDown className={`w-4 h-4 transition-transform ${isOpen ? "rotate-180" : ""}`} />
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
              const selected = value.includes(option);
              return (
                <button
                  key={option}
                  onClick={() => toggleItem(option)}
                  className={`w-full text-left px-3 py-2 transition-colors font-anta text-sm flex items-center justify-between truncate
                    ${selected ? "bg-orange/20 text-orange" : "text-text hover:bg-sidebar"}
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
