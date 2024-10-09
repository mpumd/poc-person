package com.mpumd.poc.person.sb.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Table(name = "PERSON")
@Entity
public class PersonEntity {
    @Id
    @Column(name = "id", length = 36, unique = true)
    private final String id;

    @Column(name = "first_name")
    private final String firstName;

    @Column(name = "last_name")
    private final String lastName;

    @Column(name = "gender")
    private final String gender;

    @Column(name = "birth_date")
    private final ZonedDateTime birthDate;

    @Column(name = "birth_place")
    private final String birthPlace;

    @Column(name = "nationality")
    private final String nationality;
}
