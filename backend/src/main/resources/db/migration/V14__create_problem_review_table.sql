CREATE TABLE problem_review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    note TEXT,
    reviewed_at TIMESTAMP NOT NULL,

    CONSTRAINT uq_problem_review_problem_id UNIQUE (problem_id)
);
