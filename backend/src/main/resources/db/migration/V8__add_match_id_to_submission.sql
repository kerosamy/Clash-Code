ALTER TABLE submission
    ADD COLUMN match_id BIGINT,
ADD CONSTRAINT fk_submission_match
    FOREIGN KEY (match_id)
    REFERENCES matches(id)
    ON DELETE CASCADE;
