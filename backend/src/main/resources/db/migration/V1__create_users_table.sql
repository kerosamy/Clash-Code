CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255),
                       role VARCHAR(50) NOT NULL DEFAULT 'USER',
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       img_url TEXT,
                       max_rate INT NOT NULL DEFAULT 0,
                       current_rate INT NOT NULL DEFAULT 0,
                       recovery_question VARCHAR(255),
                       recovery_answer VARCHAR(255)
);