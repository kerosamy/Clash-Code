import { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import { getSubmissionDetails } from '../../services/SubmissionsService';
import { SubmissionStatus } from '../../enums/SubmissionStatus';
import { getSubmissionStatusDisplay } from '../../utils/getSubmissionStatusDisplay';
import { getSubmissionStatusColor } from "../../utils/getSubmissionStatusColor";
import CodeMirror from '@uiw/react-codemirror';
import { javascript } from '@codemirror/lang-javascript';
import { oneDark } from '@codemirror/theme-one-dark';

interface SubmissionDetailsData {
  submissionLang: string;
  submissionCode: string;
  problemTitle: string;
  username: string;
  submissionStatus: SubmissionStatus; 
}

interface SubmissionDetailsProps {
  submissionId: number;
  isOpen: boolean;
  onClose: () => void;
}

export default function SubmissionDetails({ submissionId, isOpen, onClose }: SubmissionDetailsProps) {
  const [details, setDetails] = useState<SubmissionDetailsData | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  useEffect(() => {
    let cancelled = false;

    if (isOpen && submissionId) {
      setLoading(true);
      setError("");
      getSubmissionDetails(submissionId)
        .then((data) => {
          if (!cancelled) {      
            setDetails({
              ...data,
              submissionStatus: SubmissionStatus[data.submissionStatus as keyof typeof SubmissionStatus],
            });
          }
        })
        .catch(() => {
          if (!cancelled) setError("Failed to load submission details. Please try again."); // ← NEW
        })
        .finally(() => {
          if (!cancelled) setLoading(false); 
        });
    }
    return () => {
      cancelled = true; // ← NEW: mark as cancelled when component unmounts or closes
    };
  }, [isOpen, submissionId]);

  const fetchSubmissionDetails = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await getSubmissionDetails(submissionId);
      const detailsWithEnum: SubmissionDetailsData = {
        ...data,
        submissionStatus: SubmissionStatus[data.submissionStatus as keyof typeof SubmissionStatus],
      };
      setDetails(detailsWithEnum);
    } catch (err) {
      console.error('Error fetching submission details:', err);
      setError('Failed to load submission details. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <>
      <style>{`
        /* Custom scrollbar for CodeMirror */
        .cm-scroller::-webkit-scrollbar {
          width: 8px;
          height: 8px;
        }
        
        .cm-scroller::-webkit-scrollbar-track {
          background: rgba(255, 255, 255, 0.1);
          border-radius: 4px;
        }
        
        .cm-scroller::-webkit-scrollbar-thumb {
          background: #ff6b35;
          border-radius: 4px;
        }
        
        .cm-scroller::-webkit-scrollbar-thumb:hover {
          background: #ff8555;
        }
        
        /* For Firefox */
        .cm-scroller {
          scrollbar-width: thin;
          scrollbar-color: #ff6b35 rgba(255, 255, 255, 0.1);
        }
      `}</style>

      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
        <div className="bg-background border-2 border-orange rounded-lg max-w-4xl w-full max-h-[90vh] overflow-hidden flex flex-col">
          {/* Header */}
          <div className="relative flex items-center justify-between p-6 border-b border-orange">
            <h2 className="absolute left-1/2 transform -translate-x-1/2 text-2xl font-bold font-anta text-orange">
              Submission Details
            </h2>
            <button
              onClick={onClose}
              className="ml-auto text-text hover:text-orange transition-colors"
              aria-label="Close"
            >
              <X size={24} />
            </button>
          </div>

          {/* Content */}
          <div className="flex-1 p-6 custom-scroll overflow-y-auto space-y-6">
            {loading && (
              <div className="flex items-center justify-center py-12">
                <div className="text-text text-lg font-anta">Loading details...</div>
              </div>
            )}

            {error && (
              <div className="flex flex-col items-center justify-center py-12">
                <div className="text-statusUnsolved text-lg font-anta mb-4">{error}</div>
                <button
                  onClick={fetchSubmissionDetails}
                  className="bg-orange hover:bg-orange/90 text-white px-6 py-2 rounded-button font-anta"
                >
                  Retry
                </button>
              </div>
            )}

            {!loading && !error && details && (
              <div className="space-y-6">
                {/* Info Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="bg-background-light p-4 rounded-lg border border-orange/30">
                    <div className="text-text/70 text-sm font-anta mb-1">Problem</div>
                    <div className="text-text text-lg font-anta">{details.problemTitle}</div>
                  </div>

                  <div className="bg-background-light p-4 rounded-lg border border-orange/30">
                    <div className="text-text/70 text-sm font-anta mb-1">Username</div>
                    <div className="text-text text-lg font-anta">{details.username}</div>
                  </div>

                  <div className="bg-background-light p-4 rounded-lg border border-orange/30">
                    <div className="text-text/70 text-sm font-anta mb-1">Language</div>
                    <div className="text-text text-lg font-anta">{details.submissionLang}</div>
                  </div>

                  <div className="bg-background-light p-4 rounded-lg border border-orange/30">
                    <div className="text-text/70 text-sm font-anta mb-1">Submission Status</div>
                    <div className={`${getSubmissionStatusColor(details.submissionStatus)} text-lg font-anta`}>
                      {getSubmissionStatusDisplay(details.submissionStatus)}
                    </div>
                  </div>
                </div>

                {/* Code Editor with custom scroll */}
                <div className="bg-background-light border border-orange/30 rounded-lg overflow-hidden">
                  <CodeMirror
                    value={details.submissionCode}
                    height="400px"
                    extensions={[javascript()]}
                    theme={oneDark}
                    editable={false}
                  />
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}