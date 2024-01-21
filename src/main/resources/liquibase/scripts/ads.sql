-- liquibase formatted sql

-- changeset Ian:1
CREATE TABLE ads
(
    ad_id          SERIAL PRIMARY KEY,
    author_id      INT          NOT NULL,
    ad_price       INT          NOT NULL CHECK ( ad_price > 0),
    ad_title       VARCHAR      NOT NULL,
    ad_description VARCHAR      NOT NULL,
    image_path     VARCHAR,
    CONSTRAINT author_id_fkey FOREIGN KEY (author_id) REFERENCES users(user_id)
);