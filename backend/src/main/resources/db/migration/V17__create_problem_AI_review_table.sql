CREATE TABLE problem_ai_review (
       problem_id BIGINT PRIMARY KEY,

       problem_hash VARCHAR(255) NOT NULL,

       review_json TEXT,

       CONSTRAINT fk_problem_ai_review_problem
           FOREIGN KEY (problem_id)
               REFERENCES problem(id)
               ON DELETE CASCADE
);
