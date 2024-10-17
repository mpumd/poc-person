-- liquibase formatted sql
-- changeset mpumd:1

CREATE TABLE IF NOT EXISTS PERSON (
    id          UUID NOT NULL UNIQUE,
    first_name  VARCHAR(255),
    last_name   VARCHAR(255),
    gender      VARCHAR(255),
    birth_date  TIMESTAMP NULL, -- TODO spe datetype in all databases ?
    birth_place VARCHAR(255),
    nationality VARCHAR(255),
    PRIMARY KEY (id)
);

--INSERT INTO PERSON (id, first_name, last_name, gender, birth_date, birth_place, nationality)
--VALUES
--('123e4567-e89b-12d3-a456-426614174000', 'Alice', 'Dupont', 'Female', '1990-05-15 10:30:00', 'Paris', 'French'),
--('223e4567-e89b-12d3-a456-426614174001', 'Bob', 'Martin', 'Male', '1985-11-20 14:45:00', 'Lyon', 'French'),
--('323e4567-e89b-12d3-a456-426614174002', 'Charlie', 'Durand', 'Male', '1992-03-12 09:15:00', 'Marseille', 'French'),
--('423e4567-e89b-12d3-a456-426614174003', 'Diane', 'Leroy', 'Female', '1988-07-25 18:00:00', 'Toulouse', 'French'),
--('523e4567-e89b-12d3-a456-426614174004', 'Eve', 'Moreau', 'Female', '1995-01-30 11:00:00', 'Nice', 'French'),
--('623e4567-e89b-12d3-a456-426614174005', 'Frank', 'Garnier', 'Male', '1991-04-17 07:50:00', 'Strasbourg', 'French'),
--('723e4567-e89b-12d3-a456-426614174006', 'Grace', 'Carre', 'Female', '1993-08-22 16:20:00', 'Bordeaux', 'French'),
--('823e4567-e89b-12d3-a456-426614174007', 'Hugo', 'Gauthier', 'Male', '1989-12-01 15:30:00', 'Nantes', 'French'),
--('923e4567-e89b-12d3-a456-426614174008', 'Isabelle', 'Simon', 'Female', '1994-09-10 13:05:00', 'Montpellier', 'French'),
--('a23e4567-e89b-12d3-a456-426614174009', 'Jean', 'Benoit', 'Male', '1987-06-14 12:00:00', 'Rennes', 'French');
