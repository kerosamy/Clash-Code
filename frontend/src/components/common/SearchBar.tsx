import { Search } from "lucide-react";

interface SearchBarProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
}

export default function SearchBar({ value, onChange, placeholder = "Search by problem name" }: SearchBarProps) {
  return (
    <div className="relative w-full">
      <input
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className="w-full bg-transparent text-text/50 placeholder-text/50 px-4 py-3 pr-10 rounded-xl border border-size/30 focus:outline-none transition-colors font-anta"
      />
      <Search className="absolute right-3 top-1/2 -translate-y-1/2 text-text/50 w-5 h-5" />
    </div>
  );
}