-- liquibase formatted sql
-- changeset mpumd:1

CREATE TABLE IF NOT EXISTS PERSON (
    id          UUID NOT NULL UNIQUE,
    first_name  VARCHAR(255),
    last_name   VARCHAR(255),
    gender      VARCHAR(255),
    birth_date  VARCHAR(50), -- no interpretation of UTC offset by postgres
    birth_place VARCHAR(255),
    nationality VARCHAR(255),
    PRIMARY KEY (id)
);