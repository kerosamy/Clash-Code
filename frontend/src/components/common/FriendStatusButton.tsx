import React from "react";

type FriendStatusVariant = "negative" | "positive";

interface FriendStatusButtonProps {
    label: string;
    onClick: () => void;
    variant: FriendStatusVariant;
}

const FriendStatusButton: React.FC<FriendStatusButtonProps> = ({
    label,
    onClick,
    variant,
}) => {
    const baseClasses = `
    flex items-center justify-center
    px-4 py-2 rounded-full border-2 
    font-anta text-lg uppercase tracking-widest 
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

export default FriendStatusButton;
