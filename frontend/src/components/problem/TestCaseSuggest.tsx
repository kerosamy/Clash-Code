import React from "react";
import { Trash2 } from "lucide-react";

export interface TestCase {
  id: string;
  input: string;
  actualOutput?: string;
}

interface TestCaseItemProps {
  testCase: TestCase;
  index: number;
  onUpdate: (id: string, value: string) => void;
  onRemove: (id: string) => void;
}

const TestCaseItem: React.FC<TestCaseItemProps> = ({ testCase, index, onUpdate, onRemove }) => (
  <div className="bg-gray-800 border border-orange/30 rounded-lg p-4">
    <div className="flex justify-between items-center mb-3">
      <h3 className="text-orange font-anta text-lg">Test Case #{index + 1}</h3>
      <button 
        onClick={() => onRemove(testCase.id)} 
        className="text-red-400 hover:text-red-300 transition-colors"
        aria-label="Delete test case"
      >
        <Trash2 size={20} />
      </button>
    </div>

    {/* Input | Actual Output - Side by Side */}
    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
      {/* Input (Editable) */}
      <div>
        <label className="block text-text/70 font-anta text-sm mb-2">Input</label>
        <textarea
          value={testCase.input}
          onChange={(e) => onUpdate(testCase.id, e.target.value)}
          placeholder="Enter test input..."
          rows={6}
          className="w-full bg-background border border-orange/30 rounded-lg px-3 py-2 text-white font-mono text-sm focus:outline-none focus:border-orange resize-y custom-scroll"
        />
      </div>

      {/* Actual Output (Read-only, from backend after run) */}
      <div>
        <label className="block text-text/70 font-anta text-sm mb-2">Actual Output</label>
        <pre className="bg-background border border-green-600/30 rounded-lg px-3 py-2 text-white font-mono text-sm overflow-auto custom-scroll h-[152px]">
          {testCase.actualOutput || "Output will appear here after running tests..."}
        </pre>
      </div>
    </div>
  </div>
);

export default TestCaseItem;