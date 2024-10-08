package com.mpumd.poc.person.sb.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Table(name = "PERSON")
@Entity
public class PersonEntity {
    private final String firstName;
    private final String lastName;
    private final String gender;
    private final ZonedDateTime birthDate;
    private final String birthPlace;
    private final String nationality;
}
