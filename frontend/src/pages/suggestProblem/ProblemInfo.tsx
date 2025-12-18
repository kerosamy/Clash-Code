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
  id: number,
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
  const [saveMessage, setSaveMessage] = useState<string>("");

  // Load saved data from localStorage on mount
 useEffect(() => {
  const savedData = localStorage.getItem(STORAGE_KEY);
  if (savedData) {
    try {
      const parsedData: ProblemInfoData = JSON.parse(savedData);
      // Ensure the ID is captured and treated as a number
      if (parsedData.id !== undefined) setId(Number(parsedData.id)); 
      
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
      prev.includes(tag)
        ? prev.filter(t => t !== tag)
        : [...prev, tag]
    );
  };

  const handleSave = () => {
  const data: ProblemInfoData = {
    id, // This now uses the ID from state, preventing it from resetting to ""
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
  
  try {
    // Construct the info object directly from component state
    const info: ProblemInfoData = {
      id, // This is now a number
      solutionLang,
      timeLimit,
      memoryLimit,
      rating,
      topics: selectedTopics,
      solutionCode,
    };

    // Keep these from localStorage if they are managed by other tabs/components
    const statement = JSON.parse(localStorage.getItem("problem_statement_draft") || "{}");
    const testCases = JSON.parse(localStorage.getItem("problem_testcases_draft") || "[]");

    console.log("📦 Data to send:", { info, statement, testCases });

    // Send the structured info object
    await suggestProblemService(info, statement, testCases);
    
    alert("Problem suggestion sent successfully!");
  } catch (err: any) {
    console.error("❌ Error:", err);
    alert(err.message || "Failed to suggest problem.");
  }
};


const handleClearAll = () => {
  const confirmed = window.confirm(
    "Are you sure you want to clear all progress? This will delete your draft and reset the form."
  );

  if (confirmed) {
    // Clear the specific keys used by your application
    localStorage.removeItem(STORAGE_KEY);
    localStorage.removeItem("problem_statement_draft");
    localStorage.removeItem("problem_testcases_draft");

    // Refresh the page to clear state and start fresh
    window.location.reload();
  }
};


  const formatTagName = (tag: string) => {
    return tag
      .split("_")
      .map(word => word.charAt(0) + word.slice(1).toLowerCase())
      .join(" ");
  };

  return (
    <div className="bg-background text-white py-8">
      <div className="max-w-6xl mx-auto px-6 space-y-6">

      {/* Dynamic Status Header */}
      <div className="bg-gray-800/40 border-l-4 border-orange p-4 mb-8 rounded-r-lg shadow-sm">
        {id ? (
          <p className="text-orange font-anta text-xl">
            You're editing problem with ID: <span className="text-white font-mono bg-black/30 px-2 py-1 rounded">{id}</span>
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
            <label className="block text-orange font-anta text-lg mb-2">
              Solution Language
            </label>
            <SingleSelectDropdown
              label=""
              options={Object.values(LanguageVersion)}
              value={solutionLang}
              onChange={(value) => setSolutionLang(value as LanguageVersion)}
              placeholder="Choose solution language"
            />
          </div>

          <div>
            <label className="block text-orange font-anta text-lg mb-2">
              Time Limit (ms)
            </label>
            <input
              type="number"
              value={timeLimit}
              onChange={(e) => setTimeLimit(Number(e.target.value))}
              min="100"
              max="10000"
              step="100"
              className="w-full bg-gray-800 border border-orange/30 rounded-lg px-4 py-3 text-white font-anta focus:outline-none focus:border-orange"
            />
          </div>
        </div>

        {/* Memory + Rating */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="block text-orange font-anta text-lg mb-2">
              Memory Limit (MB)
            </label>
            <input
              type="number"
              value={memoryLimit}
              onChange={(e) => setMemoryLimit(Number(e.target.value))}
              min="16"
              max="1024"
              step="16"
              className="w-full bg-gray-800 border border-orange/30 rounded-lg px-4 py-3 text-white font-anta focus:outline-none focus:border-orange"
            />
          </div>

          <div>
            <label className="block text-orange font-anta text-lg mb-2">
              Problem Rating: {rating}
            </label>
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

        {/* Topics (Multi-select) */}
        <div>
          <label className="block text-orange font-anta text-lg mb-3">
            Problem Topics ({selectedTopics.length} selected)
          </label>
          <div className="flex flex-wrap gap-2">
            {Object.values(ProblemTags).map((tag) => (
              <button
                key={tag}
                onClick={() => handleTopicToggle(tag)}
                className={`px-4 py-2 rounded-full font-anta text-sm transition-all duration-200 ${
                  selectedTopics.includes(tag)
                    ? 'bg-orange text-white'
                    : 'bg-gray-800 text-text border border-orange/30 hover:border-orange'
                }`}
              >
                {formatTagName(tag)}
              </button>
            ))}
          </div>
        </div>

        {/* Solution Code Editor */}
        <div>
          <label className="block text-orange font-anta text-lg mb-2">
            Solution Code
          </label>
          <div className="border border-gray-700 rounded-lg overflow-hidden" style={{ height: '500px' }}>
            <Editor
              height="100%"
              language={monacoLanguageMap[solutionLang]}
              theme="vs-dark"
              value={solutionCode}
              onChange={(value) => setSolutionCode(value || "")}
              options={{
                fontSize: 16,
                minimap: { enabled: false },
                scrollBeyondLastLine: false,
                smoothScrolling: true,
                lineNumbers: "on",
                automaticLayout: true,
              }}
            />
          </div>
        </div>

        {/* Save Button and Message */}
        <div className="text-center pt-4 ">
          <button
            onClick={handleSave}
            className="bg-orange hover:bg-orange/90 text-white px-20 py-3 rounded-button text-lg font-anta transition-colors duration-200"
          >
            Save Problem Info
          </button>

          {saveMessage && (
            <p className={`mt-4 text-lg font-anta ${
              saveMessage.includes('success') ? 'text-green-400' : 'text-red-400'
            }`}>
              {saveMessage}
            </p>
          )}
        </div>
        {/* Suggest problem */}
        <div className="text-center">
            <button
                onClick={handleSuggestProblem}
                className="bg-blue-600 hover:bg-blue-700 text-white px-20 py-3 rounded-button text-lg font-anta transition-colors duration-200"
            >
                Suggest Problem
            </button>

            {/* --- Clear All / Quit Section --- */}
        <div className="pt-10 mt-10 border-t border-white/10 text-center">
          <button
            onClick={handleClearAll}
            className="bg-red-600 hover:bg-red-700 text-white px-10 py-3 rounded-button text-lg font-anta transition-all duration-200 shadow-lg shadow-red-900/20"
          >
            Clear All & Quit
          </button>
          <p className="text-gray-500 text-sm mt-3 font-anta">
            Warning: This action will permanently delete your current draft.
          </p>
        </div>

        {saveMessage && (
            <p
                className={`mt-4 text-lg font-anta ${
                    saveMessage.includes("success") ? "text-green-400" : "text-red-400"
                }`}
                >
                {saveMessage}
            </p>
        )}

        </div>
      </div>
    </div>
    
  );
}