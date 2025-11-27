import { useState } from "react";
import { LanguageVersion } from "../enums/LanguageVersion";
import SingleSelectDropdown from "../components/common/SingleSelectDropDown";


export default function Submit() {
    const [selectedLang, setSelectedLang] =  useState<LanguageVersion>(LanguageVersion.PYTHON_3_8);
    const [code, setCode] = useState<String>("")

    return (
        <div>
            <h2 className="text-2xl font-bold text-orange font-anta mb-6">
                Problem : <span className="text-white">Find The Max Value</span>
            </h2>

            <div className="mb-6 flex items-center gap-4">
                <span className="text-orange font-anta text-lg">Lang :</span>
                <SingleSelectDropdown
                    label=""
                    options={Object.values(LanguageVersion)}
                    value={selectedLang}
                    onChange={(value) => setSelectedLang(value as LanguageVersion)}
                    placeholder="Choose a language"
                />
            </div>

            <div className="mb-8">
            <h3 className="text-center text-orange font-anta text-xl font-semibold mb-3">
                Source Code :
            </h3>

            <textarea
                className="w-full h-80 bg-gray-800 text-gray-200 p-4 rounded-lg resize-none font-mono 
                        border border-gray-700 focus:outline-none focus:border-orange-400"
                placeholder="Your source code for the problem, Make sure it's valid solution"
                onChange={event => setCode(event.target.value)}
            >{code}</textarea>
            </div>

            {/* Submit Button */}
            <div className="text-center">
                <button className="bg-orange hover:bg-orange-500 text-white px-20 py-3 rounded-lg 
                                    text-lg font-anta transition">
                    Submit
                </button>
            </div>

        </div>
    );
}