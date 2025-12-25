import { useState, useEffect } from "react";
import { LanguageVersion } from "../../enums/LanguageVersion";
import { ProblemTags } from "../../enums/ProblemTags";
import SingleSelectDropdown from "../../components/common/SingleSelectDropDown";
import Editor from "@monaco-editor/react";
import { monacoLanguageMap } from "../../utils/languageMap";
import { suggestProblemService } from "../../services/ProblemService";

interface ProblemInfoProps {
  onSave?: (data: ProblemInfoData) => void;
}

export interface ProblemInfoData {
  id: number;
  name?: string;
  solutionLang: LanguageVersion;
  timeLimit: number;
  memoryLimit: number;
  rating: number;
  topics: ProblemTags[];
  solutionCode: string;
}

const STORAGE_KEY = 'problem_info_draft';

export default function ProblemInfo({ onSave }: ProblemInfoProps) {
  const [solutionLang, setSolutionLang] = useState<LanguageVersion>(LanguageVersion.PYTHON_3_8);
  const [timeLimit, setTimeLimit] = useState<number>(1000);
  const [memoryLimit, setMemoryLimit] = useState<number>(256);
  const [rating, setRating] = useState<number>(800);
  const [selectedTopics, setSelectedTopics] = useState<ProblemTags[]>([]);
  const [solutionCode, setSolutionCode] = useState<string>("");
  const [id, setId] = useState<number>(0);
  const [name, setName] = useState<string>(""); 
  const [saveMessage, setSaveMessage] = useState<string>("");

  const [errorMessage, setErrorMessage] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  useEffect(() => {
    const savedData = localStorage.getItem(STORAGE_KEY);
    if (savedData) {
      try {
        const parsedData: ProblemInfoData = JSON.parse(savedData);
        if (parsedData.id !== undefined) setId(Number(parsedData.id));
        if (parsedData.name) setName(parsedData.name);
        setSolutionLang(parsedData.solutionLang);
        setTimeLimit(parsedData.timeLimit);
        setMemoryLimit(parsedData.memoryLimit);
        setRating(parsedData.rating);
        setSelectedTopics(parsedData.topics);
        setSolutionCode(parsedData.solutionCode);
      } catch (error) {
        console.error('Failed to load from localStorage:', error);
      }
    }
  }, []);

  const handleTopicToggle = (tag: ProblemTags) => {
    setSelectedTopics(prev =>
      prev.includes(tag) ? prev.filter(t => t !== tag) : [...prev, tag]
    );
  };

  const handleSave = () => {
    const data: ProblemInfoData = {
      id,
      solutionLang,
      timeLimit,
      memoryLimit,
      rating,
      topics: selectedTopics,
      solutionCode,
    };
    
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
      setSaveMessage('Problem info saved successfully!');
      setTimeout(() => setSaveMessage(''), 3000);
    } catch (error) {
      setSaveMessage('Failed to save.');
    }
    onSave?.(data);
  };

  const handleSuggestProblem = async () => {
    console.log("🚀 Starting problem suggestion...");
    setErrorMessage("");
    setSaveMessage("");
    setIsSubmitting(true);
    
    try {
      const info: ProblemInfoData = {
        id,
        solutionLang,
        timeLimit,
        memoryLimit,
        rating,
        topics: selectedTopics,
        solutionCode,
      };

      const statement = JSON.parse(localStorage.getItem("problem_statement_draft") || "{}");
      const testCases = JSON.parse(localStorage.getItem("problem_testcases_draft") || "[]");
      const lastTestedCode = localStorage.getItem("last_compiled_code_draft") || "";

      const errors: string[] = [];
      if (!statement.title?.trim()) errors.push("Title is required.");
      if (statement.title?.length > 200) errors.push("Title exceeds 200 characters.");
      
      if (!statement.statement?.trim()) errors.push("Problem statement is required.");
      if (statement.statement?.length > 10000) errors.push("Statement exceeds 10,000 characters.");
      
      if (!statement.inputFormat?.trim()) errors.push("Input Format description is required.");
      else if (statement.inputFormat.length > 2000) errors.push("Input Format exceeds 2000 chars.");

      if (!statement.outputFormat?.trim()) errors.push("Output Format description is required.");
      else if (statement.outputFormat.length > 2000) errors.push("Output Format exceeds 2000 chars.");

      if (statement.notes?.length > 5000) errors.push("Notes exceed 5000 chars.");
      
      if (!info.solutionCode?.trim()) errors.push("Solution code is required.");
      if (info.solutionCode?.length > 50000) errors.push("Solution code exceeds 50,000 characters.");

      if (testCases.length === 0) errors.push("At least one test case is required.");
      if (testCases.length > 10) errors.push("Maximum 10 test cases allowed.");

      if (testCases.length === 0) {
        errors.push("At least one test case is required.");
      } else if (testCases.length > 10) {
        errors.push("Maximum 10 test cases allowed.");
      }

      let hasCompilationError = false;
      let hasUnrunTestCases = false;

      testCases.forEach((tc: any, index: number) => {
        if (tc.input?.length > 100000) errors.push(`Test case ${index + 1} exceeds size limit (10^5).`);
        
        if (!tc.actualOutput) {
          hasUnrunTestCases = true;
        }

        if (tc.actualOutput?.startsWith("Compilation Error")) {
          hasCompilationError = true;
        }
      });

      if (hasUnrunTestCases && testCases.length > 0) {
        errors.push("You must 'Run Test Cases' in the Test Cases tab before suggesting the problem.");
      } else if (testCases.length > 0 && info.solutionCode.trim() !== lastTestedCode.trim()) {
        errors.push("The code in the editor has changed since your last test run.");
      } else if (hasCompilationError) {
        errors.push("Reference solution has compilation errors in Test Cases. Please fix before submitting.");
      }

      if (errors.length > 0) {
        setErrorMessage(errors.join(" "));
        setIsSubmitting(false);
        return;
      }

      console.log("📦 Data to send:", { info, statement, testCases });
      
      await suggestProblemService(info, statement, testCases);
      alert("Problem suggestion sent successfully!");
 
      localStorage.removeItem(STORAGE_KEY);
      localStorage.removeItem("problem_statement_draft");
      localStorage.removeItem("problem_testcases_draft");
      window.location.reload();

    } catch (err: any) {
      console.error("❌ Error:", err);
      const backendMsg = err.response?.data || err.message;
      setErrorMessage(backendMsg);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClearAll = () => {
    const confirmed = window.confirm(
      "Are you sure you want to clear all progress? This will delete your draft and reset the form."
    );
    if (confirmed) {
      localStorage.removeItem(STORAGE_KEY);
      localStorage.removeItem("problem_statement_draft");
      localStorage.removeItem("problem_testcases_draft");
      window.location.reload();
    }
  };

  const formatTagName = (tag: string) => {
    return tag.split("_").map(word => word.charAt(0) + word.slice(1).toLowerCase()).join(" ");
  };

  return (
    <div className="bg-background text-white py-8">
      <div className="max-w-6xl mx-auto px-6 space-y-6">

        {/* Dynamic Status Header */}
        <div className="bg-gray-800/40 border-l-4 border-orange p-4 mb-8 rounded-r-lg shadow-sm">
          {id ? (
            <p className="text-orange font-anta text-xl">
              You're editing problem with Title: <span className="text-white font-mono px-2 py-1 rounded">{name}</span>
            </p>
          ) : (
            <p className="text-blue-400 font-anta text-xl">
              You're creating a <span className="text-white underline decoration-blue-400 underline-offset-4">new problem</span>
            </p>
          )}
        </div>
        
        {/* Solution Language + Time Limit */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="block text-orange font-anta text-lg mb-2">Solution Language</label>
            <SingleSelectDropdown
              label=""
              options={Object.values(LanguageVersion)}
              value={solutionLang}
              onChange={(value) => setSolutionLang(value as LanguageVersion)}
              placeholder="Choose solution language"
            />
          </div>
          <div>
            <label className="block text-orange font-anta text-lg mb-2">Time Limit (ms)</label>
            <input
              type="number"
              value={timeLimit}
              onChange={(e) => setTimeLimit(Number(e.target.value))}
              className="w-full bg-gray-800 border border-orange/30 rounded-lg px-4 py-3 text-white font-anta focus:outline-none"
            />
          </div>
        </div>

        {/* Memory + Rating */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="block text-orange font-anta text-lg mb-2">Memory Limit (MB)</label>
            <input
              type="number"
              value={memoryLimit}
              onChange={(e) => setMemoryLimit(Number(e.target.value))}
              className="w-full bg-gray-800 border border-orange/30 rounded-lg px-4 py-3 text-white font-anta focus:outline-none"
            />
          </div>
          <div>
            <label className="block text-orange font-anta text-lg mb-2">Problem Rating: {rating}</label>
            <input
              type="range"
              value={rating}
              onChange={(e) => setRating(Number(e.target.value))}
              min="100"
              max="2000"
              step="100"
              className="w-full h-2 bg-background-light rounded-lg appearance-none cursor-pointer mt-3"
              style={{
                background: `linear-gradient(to right, #ff6b35 0%, #ff6b35 ${((rating - 100) / 1900) * 100}%, rgba(255, 255, 255, 0.1) ${((rating - 100) / 1900) * 100}%, rgba(255, 255, 255, 0.1) 100%)`
              }}
            />
          </div>
        </div>

        {/* Topics */}
        <div>
          <label className="block text-orange font-anta text-lg mb-3">Problem Topics ({selectedTopics.length} selected)</label>
          <div className="flex flex-wrap gap-2">
            {Object.values(ProblemTags).map((tag) => (
              <button
                key={tag}
                onClick={() => handleTopicToggle(tag)}
                className={`px-4 py-2 rounded-full font-anta text-sm transition-all duration-200 ${
                  selectedTopics.includes(tag) ? 'bg-orange text-white' : 'bg-gray-800 text-text border border-orange/30 hover:border-orange'
                }`}
              >
                {formatTagName(tag)}
              </button>
            ))}
          </div>
        </div>

        {/* Code Editor */}
        <div>
          <label className="block text-orange font-anta text-lg mb-2">Solution Code</label>
          <span className={`text-xs font-anta ${solutionCode.length > 50000 ? 'text-red-500' : 'text-gray-500'}`}>
              {solutionCode.length}/50000
            </span>
          <div className="border border-gray-700 rounded-lg overflow-hidden" style={{ height: '500px' }}>
            <Editor
              height="100%"
              language={monacoLanguageMap[solutionLang]}
              theme="vs-dark"
              value={solutionCode}
              onChange={(value) => setSolutionCode(value || "")}
              options={{ fontSize: 16, minimap: { enabled: false }, automaticLayout: true }}
            />
          </div>
        </div>

        {/* Action Buttons */}
        <div className="text-center pt-4 space-y-4">
          <button
            onClick={handleSave}
            className="bg-orange hover:bg-orange/90 text-white px-20 py-3 rounded-button text-lg font-anta transition-colors"
          >
            Save Problem Info
          </button>
          {saveMessage && <p className="text-green-400 font-anta">{saveMessage}</p>}
        </div>

        <div className="text-center">
            <button
                onClick={handleSuggestProblem}
                disabled={isSubmitting}
                className={`bg-blue-600 hover:bg-blue-700 disabled:bg-blue-900 disabled:cursor-not-allowed text-white px-20 py-3 rounded-button text-lg font-anta transition-colors`}
            >
                {isSubmitting ? "Submitting..." : "Suggest Problem"}
            </button>

            {/* Error Message Display below button */}
            {errorMessage && (
              <div className="mt-4 max-w-2xl mx-auto p-4 bg-red-900/20 border border-red-500/50 rounded-lg">
                <p className="text-red-400 font-anta text-sm leading-relaxed text-center">
                   {errorMessage}
                </p>
              </div>
            )}

            <div className="pt-10 mt-10 border-t border-white/10 text-center">
              <button
                onClick={handleClearAll}
                className="bg-red-600 hover:bg-red-700 text-white px-10 py-3 rounded-button text-lg font-anta transition-all"
              >
                Clear All & Quit
              </button>
              <p className="text-gray-500 text-sm mt-3 font-anta">Draft will be permanently deleted.</p>
            </div>
        </div>
      </div>
    </div>
  );
}