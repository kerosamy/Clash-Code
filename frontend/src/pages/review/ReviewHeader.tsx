import AppButton from "../../components/common/AppButton";
import CounterCard from "../../components/common/CounterCard";

interface ReviewHeaderProps {
    totalProblems: number;
}

export default function ReviewHeader({totalProblems}: ReviewHeaderProps) {
    return (
        <div className="flex items-center justify-between">
            <div>
                <h1 className="text-3xl font-anta text-white tracking-wide">
                Review Problems
                </h1>
                <p className="text-text/60 text-sm mt-1 font-anta">
                Curate and manage community submissions
                </p>
            </div>
            <div className="flex items-center gap-4">
                <AppButton variant="positive" onClick={()=>{}} label = "APPROVE" size="large"  />
                <AppButton variant="negative" onClick={()=>{}} label = "REJECT" size="large"  />
                <CounterCard count={totalProblems} />
            </div>
        </div>
    )
}