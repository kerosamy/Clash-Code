// utils/validation.ts
export const PROBLEM_LIMITS = {
  TITLE: 200,
  STATEMENT: 10000,
  CODE: 50000,
  TEST_CASES: 10,
  TEST_CASE_SIZE: 100000,
};

export const validateProblemData = (info: any, statement: any, testCases: any[]) => {
  const errors: string[] = [];

  if (!statement.title || statement.title.length > PROBLEM_LIMITS.TITLE) 
    errors.push(`Title is required and must be under ${PROBLEM_LIMITS.TITLE} characters.`);
  
  if (!statement.statement || statement.statement.length > PROBLEM_LIMITS.STATEMENT)
    errors.push("Problem statement is required.");

  if (!info.solutionCode || info.solutionCode.length > PROBLEM_LIMITS.CODE)
    errors.push("Reference solution is required.");

  if (testCases.length === 0 || testCases.length > PROBLEM_LIMITS.TEST_CASES)
    errors.push(`You must provide between 1 and ${PROBLEM_LIMITS.TEST_CASES} test cases.`);

  const oversizedTC = testCases.find(tc => tc.input.length > PROBLEM_LIMITS.TEST_CASE_SIZE);
  if (oversizedTC) 
    errors.push(`Test cases must not exceed ${PROBLEM_LIMITS.TEST_CASE_SIZE} characters.`);

  return errors;
};