CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    author BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_posts_author_created_at ON posts(author, created_at DESC, id DESC);
