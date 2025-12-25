import AppButton from "../../components/common/AppButton";
import CounterCard from "../../components/common/CounterCard";
import { Sparkles } from "lucide-react"; // Assuming you use lucide-react

interface ReviewHeaderProps {
    title: string;
    totalProblems?: number;
    onApprove?: () => void;
    onReject?: () => void;
    onAIReview?: () => void;
}

export default function ReviewHeader({ 
    title, 
    totalProblems, 
    onApprove, 
    onReject, 
    onAIReview 
}: ReviewHeaderProps) {
    const isActionMode = !!(onApprove || onReject || onAIReview);

    return (
        <div className="flex items-center justify-between">
            <div>
                <h1 className="text-3xl font-anta text-white tracking-wide">{title}</h1>
                <p className="text-text/60 text-sm mt-1 font-anta">
                    Curate and manage community submissions
                </p>
            </div>
            <div className="flex items-center gap-4">
                {isActionMode ? (
                    <>
                        <button 
                            onClick={(e) => { e.preventDefault(); onAIReview?.(); }}
                            title="Show AI Review"
                            className="flex items-center justify-center w-12 h-12 rounded-full bg-gradient-to-tr from-[#4285F4] via-[#9B72CB] to-[#D96570] text-white hover:scale-110 transition-transform duration-200 shadow-lg"
                        >
                        <Sparkles size={24} fill="white" />
                        </button>
                        <AppButton variant="positive" onClick={onApprove} label="APPROVE" size="large" />
                        <AppButton variant="negative" onClick={onReject} label="REJECT" size="large" />
                        
 
                    </>
                ) : (
                    totalProblems !== undefined && <CounterCard count={totalProblems} />
                )}
            </div>
        </div>
    );
}