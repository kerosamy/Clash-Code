import { useState, useEffect } from "react";

interface ProblemStatementProps {
  onSave?: (data: ProblemStatementData) => void;
}

export interface ProblemStatementData {
  title: string;
  statement: string;
  inputFormat: string;
  outputFormat: string;
  notes: string;
}

const STORAGE_KEY = 'problem_statement_draft';

export default function ProblemStatement({ onSave }: ProblemStatementProps) {
  const [title, setTitle] = useState<string>("");
  const [statement, setStatement] = useState<string>("");
  const [inputFormat, setInputFormat] = useState<string>("");
  const [outputFormat, setOutputFormat] = useState<string>("");
  const [notes, setNotes] = useState<string>("");
  const [saveMessage, setSaveMessage] = useState<string>("");

  // Load saved data from localStorage on mount
  useEffect(() => {
    const savedData = localStorage.getItem(STORAGE_KEY);
    
    if (savedData) {
      try {
        const parsedData: ProblemStatementData = JSON.parse(savedData);
        setTitle(parsedData.title);
        setStatement(parsedData.statement);
        setInputFormat(parsedData.inputFormat);
        setOutputFormat(parsedData.outputFormat);
        setNotes(parsedData.notes);
        console.log('Loaded saved problem statement:', parsedData);
      } catch (error) {
        console.error('Failed to load from localStorage:', error);
      }
    }
  }, []);

  const handleSave = () => {
    const data: ProblemStatementData = {
      title,
      statement,
      inputFormat,
      outputFormat,
      notes,
    };
    
    // Save to localStorage
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
      console.log('Problem statement saved to localStorage:', data);
      setSaveMessage('Problem statement saved successfully!');
      
      // Clear message after 3 seconds
      setTimeout(() => setSaveMessage(''), 3000);
    } catch (error) {
      console.error('Failed to save to localStorage:', error);
      setSaveMessage('Failed to save problem statement. Please try again.');
      
      // Clear error message after 3 seconds
      setTimeout(() => setSaveMessage(''), 3000);
    }
    
    // Call parent callback if provided
    onSave?.(data);
  };

  return (
    <div className="bg-background text-white py-8">
      <div className="max-w-6xl mx-auto px-6 space-y-6">
        
        {/* Title */}
        <div>
          <label className="block text-orange font-anta text-lg mb-2">
            Problem Title
          </label>
          <span className={`text-xs font-anta ${title.length > 200 ? 'text-red-500' : 'text-gray-500'}`}>
              {title.length}/200
            </span>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Enter problem title"
            className="w-full bg-gray-800 border border-orange/30 rounded-lg px-4 py-3 text-white font-anta focus:outline-none focus:border-orange"
          />
        </div>

        {/* Statement */}
        <div>
          <label className="block text-orange font-anta text-lg mb-2">
            Problem Statement
          </label>
          <span className={`text-xs font-anta ${statement.length > 10000 ? 'text-red-500' : 'text-gray-500'}`}>
              {statement.length}/10000
            </span>
          <textarea
            value={statement}
            onChange={(e) => setStatement(e.target.value)}
            placeholder="Describe the problem in detail..."
            rows={8}
            className="w-full bg-gray-800 border border-orange/30 rounded-lg px-4 py-3 text-white font-anta focus:outline-none focus:border-orange resize-y custom-scroll"
          />
        </div>

        {/* Input Format */}
        <div>
          <label className="block text-orange font-anta text-lg mb-2">
            Input Format
          </label>
          <span className={`text-xs font-anta ${inputFormat.length > 2000 ? 'text-red-500' : 'text-gray-500'}`}>
              {inputFormat.length}/2000
            </span>
          <textarea
            value={inputFormat}
            onChange={(e) => setInputFormat(e.target.value)}
            placeholder="Describe the input format..."
            rows={6}
            className="w-full bg-gray-800 border border-orange/30 rounded-lg px-4 py-3 text-white font-anta focus:outline-none focus:border-orange resize-y custom-scroll"
          />
        </div>

        {/* Output Format */}
        <div>
          <label className="block text-orange font-anta text-lg mb-2">
            Output Format
          </label>
          <span className={`text-xs font-anta ${outputFormat.length > 2000 ? 'text-red-500' : 'text-gray-500'}`}>
              {outputFormat.length}/2000
            </span>
          <textarea
            value={outputFormat}
            onChange={(e) => setOutputFormat(e.target.value)}
            placeholder="Describe the output format..."
            rows={6}
            className="w-full bg-gray-800 border border-orange/30 rounded-lg px-4 py-3 text-white font-anta focus:outline-none focus:border-orange resize-y custom-scroll"
          />
        </div>

        {/* Notes */}
        <div>
          <label className="block text-orange font-anta text-lg mb-2">
            Notes (Optional)
          </label>
          <span className={`text-xs font-anta ${notes.length > 5000 ? 'text-red-500' : 'text-gray-500'}`}>
              {notes.length}/5000
            </span>
          <textarea
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            placeholder="Add any additional notes or constraints..."
            rows={5}
            className="w-full bg-gray-800 border border-orange/30 rounded-lg px-4 py-3 text-white font-anta focus:outline-none focus:border-orange resize-y custom-scroll"
          />
        </div>

        {/* Save Button and Message */}
        <div className="text-center pt-4 pb-6">
          <button
            onClick={handleSave}
            className="bg-orange hover:bg-orange/90 text-white px-20 py-3 rounded-button text-lg font-anta transition-colors duration-200"
          >
            Save Problem Statement
          </button>

          {saveMessage && (
            <p className={`mt-4 text-lg font-anta ${
              saveMessage.includes('success') ? 'text-green-400' : 'text-red-400'
            }`}>
              {saveMessage}
            </p>
          )}
        </div>

      </div>
    </div>
  );
}