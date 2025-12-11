interface UserRoleFilterProps {
  value: string | null;
  onChange: (value: string | null) => void;
}

export default function UserRoleFilter({
  value,
  onChange,
}: UserRoleFilterProps) {
  const roles = ["ADMIN", "USER"];

  const handleRoleClick = (role: string) => {
    if (value === role) {
      onChange(null);
    } else {
      onChange(role);
    }
  };

  return (
    <div className="flex items-center gap-4">
      <span className="text-text font-anta text-sm whitespace-nowrap">
        Filter by Role
      </span>

      <div className="flex items-center gap-2">
        {roles.map((role) => (
          <button
            key={role}
            onClick={() => handleRoleClick(role)}
            className={`
              px-4 py-2 rounded-xl border font-anta text-sm transition-all duration-200
              ${
                value === role
                  ? "bg-orange border-orange text-white"
                  : "bg-container border-sidebar/30 text-text hover:border-orange/50"
              }
            `}
          >
            {role === "ADMIN" ? "Admin" : "User"}
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