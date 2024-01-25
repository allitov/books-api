CREATE SCHEMA IF NOT EXISTS books_api_schema;

CREATE TABLE IF NOT EXISTS books_api_schema.categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS books_api_schema.books (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    author VARCHAR(256) NOT NULL,
    category_id BIGINT NOT NULL,
    publication_date DATE NOT NULL,
    FOREIGN KEY (category_id) REFERENCES books_api_schema.categories(id) ON DELETE CASCADE
);