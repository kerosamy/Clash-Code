ALTER TABLE problem
ADD COLUMN author VARCHAR(255) NULL;

UPDATE problem
SET problem_status = 'PENDING_APPROVAL'
WHERE title IN (
                'Palindrome Check',
                'Sum of Array',
                'Count Odd Numbers'
    );


UPDATE problem SET author = 'john'
WHERE title IN (
                'Palindrome Check',
                'Find Maximum',
                'Factorial'
    );

UPDATE problem SET author = 'kero'
WHERE title IN (
                'Sum of Array',
                'Count Even Numbers',
                'Fibonacci Number'
    );

UPDATE problem SET author = 'mina'
WHERE title IN (
                'Check Prime',
                'Reverse Integer',
                'Count Words'
    );

UPDATE problem SET author = 'caro'
WHERE title IN (
                'Sum of Squares',
                'Count Odd Numbers'
    );

