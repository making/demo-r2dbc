CREATE TABLE IF NOT EXISTS todo
(
    todo_id    VARCHAR(36) PRIMARY KEY,
    todo_title VARCHAR(128) NOT NULL,
    finished   BOOLEAN      NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NULL,
    INDEX todo_created_at (created_at),
    INDEX todo_updated_at (updated_at)
);