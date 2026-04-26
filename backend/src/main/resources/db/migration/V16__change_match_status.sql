-- Update all ongoing matches to completed
UPDATE matches
SET match_state = 'COMPLETED'
WHERE match_state = 'ONGOING';