-- Table for Problem
CREATE TABLE problem (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         submissions_count BIGINT NOT NULL DEFAULT 0,
                         title TEXT NOT NULL,
                         input_format TEXT NOT NULL,
                         output_format TEXT NOT NULL,
                         statement TEXT NOT NULL,
                         notes TEXT,
                         time_limit INT NOT NULL CHECK (time_limit >= 250 AND time_limit <= 10000),
                         memory_limit INT NOT NULL CHECK (memory_limit >= 4 AND memory_limit <= 512),
                         problem_status VARCHAR(50) NOT NULL DEFAULT 'PENDING_APPROVAL',
                         rate INT NOT NULL CHECK (rate % 100 = 0 AND rate >= 100 AND rate <= 2000),

    -- Embedded Solution fields
    solution_code TEXT NOT NULL,
    language_version VARCHAR(50) NOT NULL
);

-- Table for ProblemTags (ElementCollection)
CREATE TABLE problem_topics (
                                problem_id BIGINT NOT NULL,
                                tags VARCHAR(50) NOT NULL,
                                PRIMARY KEY (problem_id, tags),
                                CONSTRAINT fk_problem_topics_problem FOREIGN KEY (problem_id) REFERENCES problem(id) ON DELETE CASCADE
);

-- Table for TestCase
CREATE TABLE test_case (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           input_path TEXT,
                           output_path TEXT,
                           visible BOOLEAN NOT NULL,
                           problem_id BIGINT NOT NULL,
                           CONSTRAINT fk_test_case_problem FOREIGN KEY (problem_id) REFERENCES problem(id) ON DELETE CASCADE
);
