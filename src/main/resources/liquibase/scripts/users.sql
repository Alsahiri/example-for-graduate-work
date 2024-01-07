-- liquibase formatted sql

-- changeset Ian:1
CREATE TABLE users
(
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR NOT NULL,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    phone VARCHAR NOT NULL,
    email VARCHAR NOT NULL,
    avatar_path VARCHAR,
    avatar_size INT,
    avatar_type VARCHAR,
    pwd_hash VARCHAR NOT NULL,
    user_role VARCHAR NOT NULL
);