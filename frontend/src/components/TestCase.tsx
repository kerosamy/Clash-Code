import type { FC } from "react";
import { useState } from "react";

interface TestCase {
  input: string;
  output: string;
}

interface TestCasesProps {
  testcases: TestCase[];
}

const TestCases: FC<TestCasesProps> = ({ testcases }) => {
  const [copied, setCopied] = useState<number | null>(null);

  const handleCopy = (text: string, idx: number) => {
    navigator.clipboard.writeText(text).then(() => {
      setCopied(idx);
      setTimeout(() => setCopied(null), 1500); // Reset after 1.5s
    });
  };

  return (
    <div className="mb-6">
      <h2 className="text-problem-section font-anta text-white mb-3">
        Test Cases
      </h2>
      <div className="bg-container rounded-xl p-3">
        {testcases.map((tc, idx) => (
          <div key={idx} className="mb-6 last:mb-0">
            {/* Test Case Title */}
            <p className="font-anta text-white mb-3">Test Case {idx + 1}:</p>

            {/* Input Section */}
            <div className="mb-3">
              <span className="text-sm text-text font-anta mb-2 block">Input:</span>
              <div className="relative p-4 bg-black rounded-lg">
                <button
                  className="absolute top-2 right-2 text-xs text-white bg-gray-700 px-3 py-1 rounded hover:bg-gray-600 transition"
                  onClick={() => handleCopy(tc.input, idx * 2)}
                >
                  {copied === idx * 2 ? "Copied!" : "Copy"}
                </button>
                <pre className="font-code text-sm text-white whitespace-pre-wrap overflow-x-auto pr-20">
                  {tc.input}
                </pre>
              </div>
            </div>

            {/* Output Section */}
            <div>
              <span className="text-sm text-text font-anta mb-2 block">Output:</span>
              <div className="relative p-4 bg-black rounded-lg">
                <button
                  className="absolute top-2 right-2 text-xs text-white bg-gray-700 px-3 py-1 rounded hover:bg-gray-600 transition"
                  onClick={() => handleCopy(tc.output, idx * 2 + 1)}
                >
                  {copied === idx * 2 + 1 ? "Copied!" : "Copy"}
                </button>
                <pre className="font-code text-sm text-white whitespace-pre-wrap overflow-x-auto pr-20">
                  {tc.output}
                </pre>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default TestCases;