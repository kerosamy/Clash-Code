interface NotificationTypeFilterProps {
  value: string | null;
  onChange: (value: string | null) => void;
}

export default function NotificationTypeFilter({
  value,
  onChange,
}: NotificationTypeFilterProps) {
  const types = [
    { label: "Match", value: "MATCH" },
    { label: "Friend", value: "FRIEND" },
  ];

  const handleTypeClick = (type: string) => {
    if (value === type) {
      onChange(null);
    } else {
      onChange(type);
    }
  };

  return (
    <div className="flex items-center gap-4">
      <span className="text-text font-anta text-sm whitespace-nowrap">
        Filter by Type
      </span>

      <div className="flex items-center gap-2">
        {types.map(({ label, value: typeValue }) => (
          <button
            key={typeValue}
            onClick={() => handleTypeClick(typeValue)}
            className={`
              px-4 py-2 rounded-xl border font-anta text-sm transition-all duration-200
              ${
                value === typeValue
                  ? "bg-orange border-orange text-white"
                  : "bg-container border-sidebar/30 text-text hover:border-orange/50"
              }
            `}
          >
            {label}
          </button>
        ))}

        {value && (
          <button
            onClick={() => onChange(null)}
            className="px-3 py-2 rounded-xl bg-container border border-sidebar/30 text-text/60 hover:text-text hover:border-orange/50 font-anta text-xs transition-all duration-200"
          >
            All
          </button>
        )}
      </div>
    </div>
  );
}