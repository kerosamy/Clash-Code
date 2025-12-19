import { useState, useEffect } from "react";
import { useParams , useNavigate, useLocation} from "react-router-dom";
import { LanguageVersion } from "../../enums/LanguageVersion";
import SingleSelectDropdown from "../../components/common/SingleSelectDropDown";
import Editor from "@monaco-editor/react";
import { monacoLanguageMap } from "../../utils/languageMap";
import { submitCode } from "../../services/SubmissionsService";
import{ getProblemTitle } from "../../services/SubmissionsService";
import { getUsername } from "../../utils/jwtDecoder";
import { submitMatchCode } from "../../services/MatchService"; 

export default function Submit() {
    const { id } = useParams<{ id: string }>();
    const location = useLocation();
    const navigate = useNavigate();
    const [selectedLang, setSelectedLang] = useState<LanguageVersion>(LanguageVersion.PYTHON_3_8);
    const [code, setCode] = useState<string>("");
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
    const [submitMessage, setSubmitMessage] = useState<string>("");
    const [problemTitle, setProblemTitle] = useState<string>("");

    const isMatchRoute = location.pathname.includes("play-game");

    useEffect(() => {
        if (id) {
            getProblemTitle(parseInt(id)).then((title) => {
                setProblemTitle(title);
            }).catch(() => {
                setProblemTitle("Unknown Problem");
            });
        }
    }, [id]);


    const handleSubmit = async () => {
        if (!code.trim()) return setSubmitMessage("Please enter your code before submitting.");
        if (!id) return setSubmitMessage("Problem ID is missing.");

        setIsSubmitting(true);
        setSubmitMessage("");

        try {
            if (isMatchRoute) {
                // match submission
                submitMatchCode(Number(id), {
                problemId: Number(id),
                code,
                codeLanguage: selectedLang,
                matchId: Number(id),
                });
                setSubmitMessage("Code submitted successfully!");
                setTimeout(() => {
                navigate(`/play-game/${id}/match-state`);
                }, 500);
            } else {
                // practice submission
                submitCode(Number(id), code, selectedLang);
                setSubmitMessage("Code submitted successfully!");
                setTimeout(() => {
                navigate(`/profile/${getUsername()}/submissions`);
                }, 500);
            }
            } catch (error) {
            setSubmitMessage("Failed to submit code. Please try again.");
            } finally {
            setIsSubmitting(false);
        }
    };

     

    return (
        <div className="flex flex-col h-[calc(90vh-4rem)] p-6">
            <div className="mb-4">
                <h2 className="text-2xl font-bold text-orange font-anta">
                    Problem #{id}: <span className="text-white">{problemTitle}</span>
                </h2>
            </div>

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

            {/* MONACO EDITOR */}
            <div className="flex-1 min-h-0 mb-4 border border-gray-700 rounded-lg">
                <Editor
                    height="100%"
                    language={monacoLanguageMap[selectedLang]}
                    theme="vs-dark"
                    value={code}
                    onChange={(value) => setCode(value || "")}
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

            <div className="text-center pt-4">
                <button
                    onClick={handleSubmit}
                    disabled={isSubmitting || !code.trim()}
                    className="bg-orange hover:bg-orange/90 text-white px-20 py-3 rounded-button 
                               text-lg font-anta transition-colors duration-200 
                               disabled:opacity-50 disabled:cursor-not-allowed"
                >
                    {isSubmitting ? "Submitting..." : "Submit"}
                </button>

                {submitMessage && (
                    <p className={`mt-4 text-lg font-anta ${submitMessage.includes("success") ? "text-green-400" : "text-red-400"}`}>
                        {submitMessage}
                    </p>
                )}
            </div>
        </div>
    );
}
// import { useState, useEffect } from "react";
// import { useParams, useNavigate, useLocation } from "react-router-dom";
// import { LanguageVersion } from "../../enums/LanguageVersion";
// import SingleSelectDropdown from "../../components/common/SingleSelectDropDown";
// import Editor from "@monaco-editor/react";
// import { monacoLanguageMap } from "../../utils/languageMap";
// import { submitCode } from "../../services/SubmissionsService";
// import { getProblemTitle } from "../../services/SubmissionsService";
// import { getUsername } from "../../utils/jwtDecoder";
// // 1. IMPORT resignMatch
// import { submitMatchCode, resignMatch } from "../../services/MatchService"; 

// export default function Submit() {
//     const { id } = useParams<{ id: string }>();
//     const location = useLocation();
//     const navigate = useNavigate();
//     const [selectedLang, setSelectedLang] = useState<LanguageVersion>(LanguageVersion.PYTHON_3_8);
//     const [code, setCode] = useState<string>("");
//     const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
//     const [submitMessage, setSubmitMessage] = useState<string>("");
//     const [problemTitle, setProblemTitle] = useState<string>("");

//     const isMatchRoute = location.pathname.includes("play-game");

//     useEffect(() => {
//         if (id) {
//             getProblemTitle(parseInt(id)).then((title) => {
//                 setProblemTitle(title);
//             }).catch(() => {
//                 setProblemTitle("Unknown Problem");
//             });
//         }
//     }, [id]);

//     // 2. NEW HANDLER: Handle Resignation
//     const handleResign = async () => {
//         // Optional: Confirm before resigning to prevent accidental clicks
//         const confirmResign = window.confirm("Are you sure you want to resign? This will count as a loss.");
//         if (!confirmResign) return;

//         try {
//             if (id) {
//                 await resignMatch(Number(id));
//                 // Navigate to match state to see the result
//                 navigate(`/play-game/${id}/match-state`);
//             }
//         } catch (error) {
//             console.error("Resignation failed", error);
//             setSubmitMessage("Failed to resign. Please try again.");
//         }
//     };

//     const handleSubmit = async () => {
//         if (!code.trim()) return setSubmitMessage("Please enter your code before submitting.");
//         if (!id) return setSubmitMessage("Problem ID is missing.");

//         setIsSubmitting(true);
//         setSubmitMessage("");

//         try {
//             if (isMatchRoute) {
//                 // match submission
//                 await submitMatchCode(Number(id), {
//                     problemId: Number(id),
//                     code,
//                     codeLanguage: selectedLang,
//                     matchId: Number(id),
//                 });
//                 setSubmitMessage("Code submitted successfully!");
//                 setTimeout(() => {
//                     navigate(`/play-game/${id}/match-state`);
//                 }, 500);
//             } else {
//                 // practice submission
//                 await submitCode(Number(id), code, selectedLang);
//                 setSubmitMessage("Code submitted successfully!");
//                 setTimeout(() => {
//                     navigate(`/profile/${getUsername()}/submissions`);
//                 }, 500);
//             }
//         } catch (error) {
//             setSubmitMessage("Failed to submit code. Please try again.");
//         } finally {
//             setIsSubmitting(false);
//         }
//     };

//     return (
//         <div className="flex flex-col h-[calc(90vh-4rem)] p-6">
//             <div className="mb-4">
//                 <h2 className="text-2xl font-bold text-orange font-anta">
//                     Problem #{id}: <span className="text-white">{problemTitle}</span>
//                 </h2>
//             </div>

//             <div className="mb-4 flex items-center gap-4">
//                 <span className="text-orange font-anta text-lg">Lang:</span>
//                 <SingleSelectDropdown
//                     label=""
//                     options={Object.values(LanguageVersion)}
//                     value={selectedLang}
//                     onChange={(value) => setSelectedLang(value as LanguageVersion)}
//                     placeholder="Choose a language"
//                 />
//             </div>

//             {/* MONACO EDITOR */}
//             <div className="flex-1 min-h-0 mb-4 border border-gray-700 rounded-lg">
//                 <Editor
//                     height="100%"
//                     language={monacoLanguageMap[selectedLang]}
//                     theme="vs-dark"
//                     value={code}
//                     onChange={(value) => setCode(value || "")}
//                     options={{
//                         fontSize: 16,
//                         minimap: { enabled: false },
//                         scrollBeyondLastLine: false,
//                         smoothScrolling: true,
//                         lineNumbers: "on",
//                         automaticLayout: true,
//                     }}
//                 />
//             </div>

//             {/* 3. UPDATED BUTTON CONTAINER */}
//             <div className="flex items-center justify-center gap-6 pt-4">
//                 <button
//                     onClick={handleSubmit}
//                     disabled={isSubmitting || !code.trim()}
//                     className="bg-orange hover:bg-orange/90 text-white px-20 py-3 rounded-button 
//                                text-lg font-anta transition-colors duration-200 
//                                disabled:opacity-50 disabled:cursor-not-allowed"
//                 >
//                     {isSubmitting ? "Submitting..." : "Submit"}
//                 </button>

//                 {/* RESIGN BUTTON (Visible only in Match Mode) */}
//                 {isMatchRoute && (
//                     <button
//                         onClick={handleResign}
//                         disabled={isSubmitting}
//                         className="border-2 border-orange text-orange hover:bg-orange hover:text-white 
//                                    px-10 py-3 rounded-button text-lg font-anta transition-colors duration-200"
//                     >
//                         Resign
//                     </button>
//                 )}
//             </div>

//             {/* MESSAGE DISPLAY */}
//             <div className="text-center">
//                 {submitMessage && (
//                     <p className={`mt-4 text-lg font-anta ${submitMessage.includes("success") ? "text-green-400" : "text-red-400"}`}>
//                         {submitMessage}
//                     </p>
//                 )}
//             </div>
//         </div>
//     );
// }