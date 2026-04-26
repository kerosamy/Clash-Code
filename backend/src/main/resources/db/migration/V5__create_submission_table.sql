
CREATE TABLE submission (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            submitted_at DATETIME NOT NULL,
                            code TEXT NOT NULL,
                            language_version VARCHAR(50),
                            status VARCHAR(50) NOT NULL,
                            memory_taken INT,
                            time_taken INT,
                            number_of_test_cases INT,
                            number_of_passed_test_cases INT,
                            number_of_current_test_case INT,
                            user_id BIGINT NOT NULL,
                            problem_id BIGINT NOT NULL,
                            CONSTRAINT fk_submission_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_submission_problem FOREIGN KEY (problem_id) REFERENCES problem(id) ON DELETE CASCADE
);
