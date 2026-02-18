CREATE TABLE test_case_files (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 problem_id BIGINT NOT NULL,
                                 test_case_id BIGINT NOT NULL,
                                 file_type VARCHAR(255) NOT NULL,
                                 content TEXT,
                                 file_path VARCHAR(500) NOT NULL
);
