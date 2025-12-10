
-- Create matches table
CREATE TABLE matches (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         start_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         duration INT NOT NULL,
                         match_state VARCHAR(50) NOT NULL,
                         game_mode VARCHAR(50) NOT NULL,
                         problem_id BIGINT NOT NULL,
                         CONSTRAINT fk_matches_problem FOREIGN KEY (problem_id) REFERENCES problem(id)
);

-- Create match_participant table
CREATE TABLE match_participant (
                                   user_id BIGINT NOT NULL,
                                   match_id BIGINT NOT NULL,
                                   player_rank INT,
                                   rate_change INT,
                                   new_rating INT,
                                   PRIMARY KEY (user_id, match_id),
                                   CONSTRAINT fk_match_participant_user FOREIGN KEY (user_id) REFERENCES users(id),
                                   CONSTRAINT fk_match_participant_match FOREIGN KEY (match_id) REFERENCES matches(id)
);
