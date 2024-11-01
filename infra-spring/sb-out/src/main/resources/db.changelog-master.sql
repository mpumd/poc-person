-- liquibase formatted sql

-- changeset mpumd:1
-------------------------------------------------------------------------------
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

-- changeset mpumd:2
-------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS GENDERS (
	person_id uuid  NOT NULL,
	change_date     TIMESTAMP(6) NOT NULL,
	gender          VARCHAR(255) NOT NULL,
	CONSTRAINT genders_pkey PRIMARY KEY (change_date, person_id)
	-- INFO constraint on the kind of value but it's the context that check the value, not the infra which is the DB here
	-- CONSTRAINT genders_gender_check CHECK (((gender)::text = ANY ((ARRAY['MALE'::character varying, 'FEMALE'::character varying, 'HERMAPHRODITE'::character varying, 'ALIEN'::character varying])::text[]))),
);

ALTER TABLE GENDERS ADD CONSTRAINT genders_person_id_fk FOREIGN KEY (person_id) REFERENCES person(id);