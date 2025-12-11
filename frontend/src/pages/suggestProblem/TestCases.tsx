import React, { useState, useEffect } from "react";
import { Plus, Play } from "lucide-react";
import TestCaseItem from "../../components/problem/TestCaseSuggest";

export interface TestCase {
  id: string;
  input: string;
  actualOutput?: string;
  visible: boolean;
}

interface TestCasesProps {
  onSave?: (testCases: TestCase[]) => void;
  onRun?: (testCases: TestCase[]) => Promise<TestCase[]>;
}


const STORAGE_KEY = "problem_testcases_draft";

const TestCases: React.FC<TestCasesProps> = ({ onSave, onRun }) => {
  const [testCases, setTestCases] = useState<TestCase[]>([]);
  const [saveMessage, setSaveMessage] = useState<string>("");
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

    try {
      if (onRun) {
        const results = await onRun(testCases);
        setTestCases(results);
        setSaveMessage("Test cases executed successfully!");
      } else {
        setTimeout(() => {
          const updated = testCases.map((tc) => ({
            ...tc,
            actualOutput: `Output for: ${tc.input}`,
          }));
          setTestCases(updated);
          setSaveMessage("Test cases executed successfully!");
          setRunningTests(false);
        }, 1500);
        return;
      }
    } catch (error) {
      console.error("Failed to run test cases:", error);
      setSaveMessage("Failed to run test cases. Please try again.");
    } finally {
      setRunningTests(false);
      setTimeout(() => setSaveMessage(""), 3000);
    }
  };

  const handleSave = () => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(testCases));
      setSaveMessage("Test cases saved successfully!");
      setTimeout(() => setSaveMessage(""), 3000);
      onSave?.(testCases);
    } catch (error) {
      console.error("Failed to save test cases:", error);
      setSaveMessage("Failed to save test cases. Please try again.");
      setTimeout(() => setSaveMessage(""), 3000);
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

        {saveMessage && (
          <p
            className={`text-center text-lg font-anta ${
              saveMessage.includes("success") || saveMessage.includes("executed")
                ? "text-green-400"
                : "text-red-400"
            }`}
          >
            {saveMessage}
          </p>
        )}
      </div>
    </div>
  );
};

export default TestCases;
