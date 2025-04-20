SET client_encoding TO utf8;

CREATE TABLE IF NOT EXISTS project112.person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL
);