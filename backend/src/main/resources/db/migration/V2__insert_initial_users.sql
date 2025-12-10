-- Insert initial users
INSERT INTO users (username, email, password, is_admin, max_rate, current_rate)
VALUES
    ('el-nagar', 'elnagar@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', TRUE, 5, 4),
    ('kero', 'kero@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', TRUE, 1500, 1230),
    ('john', 'john@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', FALSE, 350, 200),
    ('caro', 'caro@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', FALSE, 100, 100),
    ('miky', 'miky@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', TRUE, 650, 650),
    ('jana', 'jana@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', FALSE, 1000, 1200),
    ('mina', 'mina@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', FALSE, 2000, 2000);
