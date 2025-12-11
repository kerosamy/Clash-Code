INSERT INTO notifications (type, sender_id, recipient_id, title, message, is_read) VALUES
('FRIEND_REQUEST_RECEIVED', 2, 1, 'New Friend Request', 'You have received a new friend request', FALSE),
('FRIEND_REQUEST_ACCEPTED', 2, 1, 'Friend Request Accepted', 'Your friend request was accepted!', FALSE),
('TEAM_INVITATION', 5, 1, 'Team Invitation', 'You have been invited to join a team', TRUE),
('MATCH_COMPLETED', 1, 2, 'Match Completed', 'Your match has ended', FALSE),
('FRIEND_REQUEST_RECEIVED', 3, 2, 'New Friend Request', 'You have received a new friend request', FALSE),
('MATCH_COMPLETED', 1, 2, 'Victory!', 'Congratulations! You won the match', TRUE),
('TEAM_INVITATION', 4, 3, 'Team Invitation', 'You have been invited to join a team', FALSE),
('FRIEND_REQUEST_ACCEPTED', 2, 3, 'Friend Request Accepted', 'Your friend request was accepted!', TRUE),
('MATCH_COMPLETED', 1, 3, 'Match Completed', 'Your match has ended', FALSE),
('MATCH_INVITATION', 1, 4, 'Match Invitation', 'You have been challenged to a match!', FALSE),
('MATCH_STATUS', 2, 4, 'Match In Progress', 'Your match is currently ongoing', FALSE),
('TEAM_INVITATION', 3, 5, 'Team Invitation', 'Join the elite coding team!', FALSE);