CREATE TABLE IF NOT EXISTS todo
(
    todo_id    VARCHAR(36) PRIMARY KEY,
    todo_title VARCHAR(128) NOT NULL,
    finished   BOOLEAN      NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NULL
);
CREATE INDEX todo_created_at ON todo (created_at);
CREATE INDEX todo_updated_at ON todo (updated_at);