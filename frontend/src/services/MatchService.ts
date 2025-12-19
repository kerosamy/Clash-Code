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