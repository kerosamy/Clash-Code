import React from "react";

type FriendStatusVariant = "negative" | "positive";

interface AppButtonProps {
    label: string;
    onClick: (e: any) => void;
    variant: FriendStatusVariant;
    size: "small" | "large";
}``

const AppButton: React.FC<AppButtonProps> = ({
    label,
    onClick,
    variant,
    size = "large",
}) => {
    const sz = size === "small" ? "px-4 py-1 text-[10px]"
                  : "px-4 py-2 text-lg";

    const baseClasses = `
    flex items-center justify-center
    rounded-full border-2 ${sz}
    font-anta uppercase tracking-widest 
    transition-all duration-300 hover:text-white
    `;

    const variants: Record<FriendStatusVariant, string> = {
        negative: `
        border-rose-500/30 bg-rose-500/5 text-rose-400
        hover:bg-rose-600 hover:border-rose-600 
        hover:shadow-[0_0_10px_rgba(225,29,72,0.3)]
        `,
        positive: `
        border-emerald-500/30 bg-emerald-500/5 text-emerald-400
        hover:bg-emerald-500 hover:border-emerald-500 
        hover:shadow-[0_0_10px_rgba(16,185,129,0.3)]
        `
    };

    return (
        <button onClick={onClick} className={`${baseClasses} ${variants[variant]}`}>
            {label}
        </button>
    );
};

export default AppButton;
