import axios from "axios";

const API_BASE = "http://localhost:8080/problem";

export async function fetchProblems(page = 0, size = 10) {
  const response = await axios.get(`${API_BASE}/browse`, {
    params: { page, size },
  });

  return response.data; // returned Page<ProblemListDto>
}

export async function fetchProblemById(id: number) {
  try {
    const response = await axios.get(`${API_BASE}/${id}`);
    return response.data; // returned ProblemDto
  } catch (error) {
    console.error("Error fetching problem:", error);
    throw error;
  }
}