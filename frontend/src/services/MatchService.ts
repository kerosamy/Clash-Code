import { apiRequest } from "./api";
import type { ProblemTags } from "../enums/ProblemTags";

export interface ProblemRequestDto {
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

export interface CreateMatchRequestDto {
  player1Id: number;
  player2Id: number;
  gameMode: string;   
  problemId: number;
  duration: number;
}

export interface MatchParticipantDto {
  userId: number;
  rank: number;
  rateChange: number;
  newRating: number;
}

export interface MatchResponseDto {
  id: number;
  startAt: string;    
  duration: number;
  matchState: string; 
  problemId: number;
  participants: MatchParticipantDto[];
}

export interface SubmissionRequestDto {
  problemId: number;
  code: string;
  codeLanguage: string;
  matchId?: number;  
}

export interface SubmissionLogEntryDto {
  submissionId: number;
  status: string;
  submittedAt: string;
  numberOfPassedTestCases: number;
  numberOfTotalTestCases: number;
  numberOfCurrentTestCase?: number;
}

export interface MatchSubmissionLogDto {
  username: string;
  avatarUrl: string;
  rank: string;
  submissions: SubmissionLogEntryDto[];
}

export interface MatchResultDto {
    rated: boolean;
    username: string;
    avatarUrl: string;
    rank: number;     
    rateChange: number;
    newRating: number;
}

export interface MatchHistoryDto {
    matchId: number;
    time: string;
    opponent: string;
    problem: string;
    rank: number;
    rateChange: number;
    newRating: number;
    rated: boolean;
}

export async function createMatch(
  body: CreateMatchRequestDto
): Promise<MatchResponseDto> {
  return apiRequest<MatchResponseDto>({
    method: "POST",
    url: "/matches/create",
    data: body,
  });
}

export async function submitMatchCode(
  matchId: number,
  body: SubmissionRequestDto
): Promise<void> {

  return apiRequest<void>({
    method: "POST",
    url: `/matches/${matchId}/submit`,
    data: body,
  });
}

export async function getMatchSubmissionLog(
  matchId: number
): Promise<MatchSubmissionLogDto[]> {
  return apiRequest<MatchSubmissionLogDto[]>({
    method: "GET",
    url: `/matches/${matchId}/submission-log`,
  });
}

export async function resignMatch(matchId: number): Promise<void> {
  return apiRequest<void>({
    method: "POST",
    url: `/matches/${matchId}/resign`,
  });
}

export async function getProblemForMatch(matchId: number): Promise<ProblemRequestDto> {
  return apiRequest<ProblemRequestDto>({
    method: "GET",
    url: `/matches/${matchId}/problem`,
  });
}

export async function getMatchDetails(matchId: number): Promise<MatchResponseDto> {
  return apiRequest<MatchResponseDto>({
    method: "GET",
    url: `/matches/${matchId}`,
  });
}

export async function getMatchResults(matchId: number): Promise<MatchResultDto> {
    return apiRequest<MatchResultDto>({
        method: "GET",
        url: `/matches/${matchId}/results`,
    });
}


export async function searchOpponent(): Promise<void> {
  return apiRequest<void>({
    method: "POST",
    url: "/matches/search-opponent",
  });
}


export async function cancelOpponentSearch(): Promise<void> {
  return apiRequest<void>({
    method: "DELETE",
    url: "/matches/search-opponent/cancel",
  });
}
export async function getMatchHistory(
  page: number = 0, 
  size: number = 10,
  filter: string | null = "All Matches" 
): Promise<{ content: MatchHistoryDto[]; totalPages: number }> {
  
    let ratedParam = null;
    if (filter === "Rated Only") ratedParam = true;
    if (filter === "Friendly Only") ratedParam = false;

    return apiRequest<{ content: MatchHistoryDto[]; totalPages: number }>({
        method: "GET",
        url: "/matches/my-history",
        params: { 
            page, 
            size, 
            rated: ratedParam 
        }
    });
}

export async function getOnGoingMatch(): Promise<number | null> {
  return apiRequest<number | null>({
    method: "GET",
    url: "/matches/on-going",
  });
}
