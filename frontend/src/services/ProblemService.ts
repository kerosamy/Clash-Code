import type { ProblemTags } from "../enums/ProblemTags";
import { apiRequest } from "./api";
import type { ProblemInfoData } from "../pages/suggestProblem/ProblemInfo";
import type { ProblemStatementData } from "../pages/suggestProblem/ProblemStatment";
import type { TestCase } from "../pages/suggestProblem/TestCases";



// Pagination wrapper 
interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

interface ProblemListDto {
  id: number;
  title: string;
  rate: number;
  tags: ProblemTags[];
  rejectionNote: string;
  status: string;
}


interface VisibleTestCase {
  id: string ;
  input: string;
  output: string;
}

interface TestcaseRunRequestDto {
  stdin: string[];
  sourceCode: string;
  language: string;
  timeLimit: number;
  memoryLimit: number;
}

export interface ProblemDto {
  id: number;
  author: string;
  title: string;
  statement: string;      // Mapping to 'statement' in JSON
  inputFormat: string;
  outputFormat: string;
  notes: string;
  timeLimit: number;
  memoryLimit: number;
  rate: number;           // The JSON uses 'rate'
  tags: string[];         // The JSON shows an array of strings like ['STRINGS']
  visibleTestCases: VisibleTestCase[];
  submissionsCount: number;
  solutionCode: string | null;
  solutionLanguage: string | null;
}

export async function fetchProblems(
  page = 0,
  size = 20
): Promise<Page<ProblemListDto>> {
  return apiRequest<Page<ProblemListDto>>({
    method: "GET",
    url: "/problem/browse",
    params: { page, size },
  });
}

export async function fetchFilteredProblems(
  tags: ProblemTags[] = [],
  minRate?: number,
  maxRate?: number,
  page = 0,
  size = 20
): Promise<Page<ProblemListDto>> {
  const payload: Record<string, unknown> = { tags };
  if (minRate !== undefined) payload.minRate = minRate;
  if (maxRate !== undefined) payload.maxRate = maxRate;

  return apiRequest<Page<ProblemListDto>>({
    method: "POST",
    url: "/problem/browse/filter",
    data: payload,
    params: { page, size },
  });
}

export async function searchProblemsByTitle(
  keyword: string,
  page = 0,
  size = 20
): Promise<Page<ProblemListDto>> {
  return apiRequest<Page<ProblemListDto>>({
    method: "GET",
    url: "/problem/search",
    params: { keyword, page, size },
  });
}

export async function fetchProblemById(id: number): Promise<ProblemDto> {
  return apiRequest<ProblemDto>({
    method: "GET",
    url: `/problem/${id}`,
  });
}

export async function fetchFullProblemById(id: number): Promise<ProblemDto> {
  return apiRequest<ProblemDto>({
    method: "GET",
    url: `/problem/draft/${id}`,
  });
}

export interface ProblemRequestDto {
  id: number | null;
  title: string;
  inputFormat: string;
  outputFormat: string;
  statement: string;
  notes: string;
  mainSolution: string;
  solutionLanguage: string;
  timeLimit: number;
  memoryLimit: number;
  rate: number;
  tags: ProblemTags[];
  visibleFlags: boolean[];
}
export async function suggestProblemService(
  info: ProblemInfoData,
  statement: ProblemStatementData,
  testCases: TestCase[]
): Promise<void> {
  if (!info || !statement || !testCases) {
    throw new Error("Missing required problem data or test cases.");
  }

  const visibleFlags = testCases.map((tc) => tc.visible ?? true);

  const dto: ProblemRequestDto = {
    id: info.id,
    title: statement.title || "Untitled Problem",
    inputFormat: statement.inputFormat || "",
    outputFormat: statement.outputFormat || "",
    statement: statement.statement || "",
    notes: statement.notes || "",
    mainSolution: info.solutionCode || "",
    solutionLanguage: info.solutionLang,
    timeLimit: info.timeLimit,
    memoryLimit: info.memoryLimit,
    rate: info.rating,
    tags: info.topics,
    visibleFlags,
  };

  // Convert test cases to files
  const files: File[] = testCases.map((tc, index) => {
    const blob = new Blob([tc.input || ""], { type: "text/plain" });
    return new File([blob], `testcase_${index + 1}.txt`, { type: "text/plain" });
  });

  // Prepare FormData
  const formData = new FormData();
  
  // FIX: Send as Blob with proper Content-Type header
  // This mimics what Postman does when you select "application/json"
  const problemBlob = new Blob(
    [JSON.stringify(dto)], 
    { type: "application/json" }
  );
  
  // Important: Add a filename to help Spring recognize it properly
  formData.append("problem", problemBlob, "problem.json");

  // Append test cases
  files.forEach((file) => {
    formData.append("testcases", file);
  });

  console.log("📤 Sending request...");

  // Send request
  await apiRequest({
    method: "POST",
    url: "/problem/suggest",
    data: formData,
  });

  console.log("✅ Request sent successfully");

  // Clear localStorage
  localStorage.removeItem("problem_info_draft");
  localStorage.removeItem("problem_statement_draft");
  localStorage.removeItem("problem_testcases_draft");
}

export async function runTestCasesService(
  stdin: string[],
  sourceCode: string,
  language: string,
  timeLimit: number,
  memoryLimit: number
): Promise<string[]> {
  const payload: TestcaseRunRequestDto = {
    stdin,
    sourceCode,
    language,
    timeLimit,
    memoryLimit,
  };

  return apiRequest<string[]>({
    method: "POST",
    url: "/problem/run-test-cases",
    data: payload,
  });
}

export async function fetchMySuggestions(
  page = 0,
  size = 20,
  status: string | null = null
): Promise<Page<ProblemListDto>> {
  const params: any = { page, size };
  if (status) {
    params.status = status;
  }

  return apiRequest<Page<ProblemListDto>>({
    method: "GET",
    url: "/problem/my-suggestions",
    params: params,
  });
}