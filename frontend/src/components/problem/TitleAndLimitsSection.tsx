
export interface TitleAndLimitsProps {
    title: string;
    timeLimit: number; // in milliseconds
    memoryLimit: number; // in megabytes
}

export default function TitleAndLimitsSection(
    problem: TitleAndLimitsProps
) {
    return (
        <div className="mb-8 text-center">
          <h1 className="text-3xl mb-3">{problem.title}</h1>
          <div className="text-sm text-text space-y-1">
            <p>Time Limit Per Test: {problem.timeLimit} MS</p>
            <p>Memory Limit Per Test: {problem.memoryLimit} MB</p>
          </div>
        </div>
    )
}