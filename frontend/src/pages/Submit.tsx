
export default function Submit() {
    return (
        <div>

            {/* Title */}
            <h2 className="text-2xl font-bold text-orange font-anta mb-6">
            Problem : <span className="text-white">Find The Max Value</span>
            </h2>

            {/* Language Selector */}
            <div className="mb-6">
            <span className="text-orange font-anta text-lg">Lang :</span>
            <select className="ml-4 px-3 py-2 bg-[#0f131a] text-white rounded-lg border border-gray-600">
                <option>Java 21</option>
            </select>
            </div>
            {/* Code Editor */}
            <div className="mb-8">
            <h3 className="text-center text-orange font-anta text-xl font-semibold mb-3">
                Source Code :
            </h3>

            <textarea
                className="w-full h-80 bg-black text-gray-200 p-4 rounded-lg resize-none font-mono 
                        border border-gray-700 focus:outline-none focus:border-orange-400"
                placeholder="Your source code for the problem, Make sure it's valid solution"
            ></textarea>
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