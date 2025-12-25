import React, { useState, useEffect } from "react";
import { Plus, Play } from "lucide-react";
import TestCaseItem from "../../components/problem/TestCaseSuggest";
import { runTestCasesService } from "../../services/ProblemService";
import type { ProblemInfoData } from "./ProblemInfo";

export interface TestCase {
  id: string;
  input: string;
  actualOutput?: string;
  visible: boolean;
}

interface TestCasesProps {
  onSave?: (testCases: TestCase[]) => void;
}

const STORAGE_KEY = "problem_testcases_draft";
const INFO_STORAGE_KEY = "problem_info_draft";

const TestCases: React.FC<TestCasesProps> = ({ onSave }) => {
  const [testCases, setTestCases] = useState<TestCase[]>([]);
  const [saveMessage, setSaveMessage] = useState<string>("");
  const [errorMessage, setErrorMessage] = useState<string>(""); 
  const [runningTests, setRunningTests] = useState<boolean>(false);

  useEffect(() => {
    const savedData = localStorage.getItem(STORAGE_KEY);
    if (savedData) {
      try {
        setTestCases(JSON.parse(savedData));
      } catch {
        addTestCase();
      }
    } else {
      addTestCase();
    }
  }, []);

  const addTestCase = () => {
    setTestCases((prev) => [
      ...prev,
      { id: `test_${Date.now()}`, input: "", visible: true },
    ]);
  };

  const removeTestCase = (id: string) => {
    setTestCases((prev) => prev.filter((tc) => tc.id !== id));
  };

  const updateTestCase = (id: string, value: string) => {
    setTestCases((prev) =>
      prev.map((tc) => (tc.id === id ? { ...tc, input: value } : tc))
    );
  };

  const toggleVisibility = (id: string) => {
    setTestCases((prev) =>
      prev.map((tc) => (tc.id === id ? { ...tc, visible: !tc.visible } : tc))
    );
  };

  const runTestCases = async () => {
    setRunningTests(true);
    setSaveMessage("");
    setErrorMessage(""); 

    try {
      const infoStr = localStorage.getItem(INFO_STORAGE_KEY);
      if (!infoStr) {
        throw new Error("Missing Problem Info. Please save it first.");
      }
      
      const info: ProblemInfoData = JSON.parse(infoStr);
      
      if (!info.solutionCode || !info.solutionCode.trim()) {
        throw new Error("Solution code is empty. Please go back and write your solution.");
      }

      const inputs = testCases.map(tc => tc.input);

      const outputs = await runTestCasesService(
        inputs,
        info.solutionCode,
        info.solutionLang,
        info.timeLimit,
        info.memoryLimit
      );

      let compilationErrorMessage: string | null = null;

      for (const output of outputs) {
        if (output.startsWith("Compilation Error")) {
          compilationErrorMessage = output;
          break;
        }
      }

      const updated = testCases.map((tc, index) => ({
        ...tc,
        actualOutput: compilationErrorMessage || outputs[index] 
      }));

      setTestCases(updated);
      setSaveMessage("Test cases executed successfully!");
      localStorage.setItem("last_compiled_code_draft", info.solutionCode);

      localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
    } catch (error: any) {
      console.error("Failed to run test cases:", error);
      const msg = error.response?.data?.message || error.message || "An unexpected error occurred.";
      setErrorMessage(msg);
    } finally {
      setRunningTests(false);
      setTimeout(() => setSaveMessage(""), 3000);
    }
  };

  const handleSave = () => {
    setErrorMessage("");
    setSaveMessage("");
    
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(testCases));
      console.log(testCases);
      setSaveMessage("Test cases saved successfully!");
      setTimeout(() => setSaveMessage(""), 3000);
      onSave?.(testCases);
    } catch (error: any) {
      console.error("Failed to save test cases:", error);
      setErrorMessage(error.message || "Failed to save test cases.");
    }
  };

  return (
    <div className="bg-background text-white py-8">
      <div className="max-w-6xl mx-auto px-6 space-y-6"> 
        {testCases.map((tc, index) => (
          <TestCaseItem
            key={tc.id}
            testCase={tc}
            index={index}
            onUpdate={updateTestCase}
            onRemove={removeTestCase}
            onToggleVisibility={toggleVisibility}
          />
        ))}

        <button
          onClick={addTestCase}
          className="w-full bg-gray-800 hover:bg-gray-700 border border-orange/30 hover:border-orange rounded-lg px-4 py-3 text-text hover:text-orange font-anta transition-colors flex items-center justify-center gap-2"
        >
          <Plus size={20} /> Add Test Case
        </button>

        <div className="flex flex-col sm:flex-row gap-4 pt-4">
          <button
            onClick={runTestCases}
            disabled={runningTests || testCases.length === 0}
            className="flex-1 bg-green-600 hover:bg-green-700 disabled:bg-gray-600 disabled:cursor-not-allowed text-white px-6 py-3 rounded-button font-anta transition-colors flex items-center justify-center gap-2"
          >
            <Play size={20} />
            {runningTests ? "Running Tests..." : "Run Test Cases"}
          </button>

          <button
            onClick={handleSave}
            disabled={testCases.length === 0}
            className="flex-1 bg-orange hover:bg-orange/90 disabled:bg-gray-600 disabled:cursor-not-allowed text-white px-6 py-3 rounded-button font-anta transition-colors"
          >
            Save Test Cases
          </button>
        </div>

        {(saveMessage || errorMessage) && (
          <p
            className={`text-center text-lg font-anta ${
              errorMessage ? "text-red-400" : "text-green-400"
            }`}
          >
            {errorMessage || saveMessage}
          </p>
        )}
      </div>
    </div>
  );
};

export default TestCases;