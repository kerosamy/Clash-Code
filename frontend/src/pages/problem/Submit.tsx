import { useState } from "react";
import { useParams } from "react-router-dom";
import { LanguageVersion } from "../../enums/LanguageVersion";
import SingleSelectDropdown from "../../components/common/SingleSelectDropDown";

export default function Submit() {
    const { id } = useParams<{ id: string }>();
    const [selectedLang, setSelectedLang] = useState<LanguageVersion>(LanguageVersion.PYTHON_3_8);
    const [code, setCode] = useState<string>("");


    const problemTitle = "Find The Max Value"; 

    return (
        <div className="flex flex-col h-[calc(90vh-4rem)] p-6">
            {/* Header */}
            <div className="mb-4">
                <h2 className="text-2xl font-bold text-orange font-anta">
                    Problem #{id}: <span className="text-white">{problemTitle}</span>
                </h2>
            </div>

            {/* Language Selector */}
            <div className="mb-4 flex items-center gap-4">
                <span className="text-orange font-anta text-lg">Lang:</span>
                <SingleSelectDropdown
                    label=""
                    options={Object.values(LanguageVersion)}
                    value={selectedLang}
                    onChange={(value) => setSelectedLang(value as LanguageVersion)}
                    placeholder="Choose a language"
                />
            </div>

            {/* Code Editor - Flexible height */}
            <div className="flex-1 flex flex-col mb-4 min-h-0">
                <h3 className="text-center text-orange font-anta text-xl font-semibold mb-3">
                    Source Code:
                </h3>

                <textarea
                    className="flex-1 w-full bg-gray-800 text-gray-200 p-4 rounded-lg resize-none font-mono 
                            border border-gray-700 focus:outline-none focus:border-orange-400 custom-scroll"
                    placeholder="Your source code for the problem. Make sure it's a valid solution."
                    value={code}
                    onChange={(event) => setCode(event.target.value)}
                />
            </div>

            {/* Submit Button */}
            <div className="text-center pt-4">
                <button 
                    className="bg-orange hover:bg-orange/90 text-white px-20 py-3 rounded-button 
                            text-lg font-anta transition-colors duration-200"
                >
                    Submit
                </button>
            </div>
        </div>
    );
}