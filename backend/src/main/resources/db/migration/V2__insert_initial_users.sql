-- Insert initial users
INSERT INTO users (username, email, password, role, max_rate, current_rate)
VALUES
    ('admin', 'admin@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', 'SUPER_ADMIN', 1000, 200),
    ('el-nagar', 'elnagar@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', 'ADMIN', 5, 4),
    ('kero', 'kero@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', 'ADMIN', 1500, 1230),
    ('john', 'john@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', 'USER', 350, 200),
    ('caro', 'caro@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', 'USER', 100, 100),
    ('miky', 'miky@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', 'ADMIN', 650, 650),
    ('jana', 'jana@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', 'USER', 1000, 1200),
    ('mina', 'mina@gmail.com', '$2a$10$B2dVFmw1ptPbc.eCtkwKO.5knKfvI7kKHbPysc68m6r6KoUbO94NW', 'USER', 2000, 2000);
