CREATE TABLE friend (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,

                        sender_id BIGINT NOT NULL,
                        receiver_id BIGINT NOT NULL,

                        status VARCHAR(50),

                        requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT uk_sender_receiver UNIQUE (sender_id, receiver_id),

                        CONSTRAINT fk_friend_sender
                            FOREIGN KEY (sender_id) REFERENCES users(id),

                        CONSTRAINT fk_friend_receiver
                            FOREIGN KEY (receiver_id) REFERENCES users(id)
);