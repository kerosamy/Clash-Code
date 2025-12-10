ALTER TABLE problem
ADD COLUMN author VARCHAR(255) NULL;

UPDATE problem
SET problem_status = 'PENDING_APPROVAL'
WHERE title IN (
                'Palindrome Check',
                'Sum of Array',
                'Find Maximum',
                'Count Even Numbers',
                'Factorial',
                'Fibonacci Number',
                'Check Prime',
                'Reverse Integer',
                'Count Words',
                'Sum of Squares',
                'Count Odd Numbers'
    );