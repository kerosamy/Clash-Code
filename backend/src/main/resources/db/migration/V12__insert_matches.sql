-- ===================== MATCH 1 =====================
INSERT INTO matches (start_at, duration, match_state, game_mode, problem_id)
VALUES ('2025-12-10 09:00:00', 30, 'ONGOING', 'RATED', 1);

-- Match participants for MATCH 1
INSERT INTO match_participant (user_id, match_id, player_rank, rate_change, new_rating)
VALUES (1, 1, NULL, NULL, NULL),  -- Player 1 (kero)
       (2, 1, NULL, NULL, NULL);  -- Player 2 (john)

-- ===================== MATCH 2 =====================
INSERT INTO matches (start_at, duration, match_state, game_mode, problem_id)
VALUES ('2025-12-10 10:00:00', 45, 'ONGOING', 'UNRATED', 2);

-- Match participants for MATCH 2
INSERT INTO match_participant (user_id, match_id, player_rank, rate_change, new_rating)
VALUES (3, 2, NULL, NULL, NULL),  -- Player 3 (caro)
       (4, 2, NULL, NULL, NULL);  -- Player 4 (miky)

-- ===================== MATCH 3 =====================
INSERT INTO matches (start_at, duration, match_state, game_mode, problem_id)
VALUES ('2025-12-10 11:00:00', 60, 'ONGOING', 'RATED', 3);

-- Match participants for MATCH 3
INSERT INTO match_participant (user_id, match_id, player_rank, rate_change, new_rating)
VALUES (5, 3, NULL, NULL, NULL),  -- Player 5 (jana)
       (6, 3, NULL, NULL, NULL);  -- Player 6 (mina)
