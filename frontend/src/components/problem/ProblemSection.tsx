import type { ReactNode } from "react";


interface ProblemSectionProps {
  header: string;
  children: ReactNode; // The body/content of the section
}

export default function ProblemSection({ header, children }: ProblemSectionProps) {
  return (
    <div className="w-full mb-4">
      <h2 className="font-anta mb-2 text-problem-section ml-2">{header}</h2>
      <div className="font-code p-4 bg-container rounded-xl">
        {children}
      </div>
    </div>
  );
}
