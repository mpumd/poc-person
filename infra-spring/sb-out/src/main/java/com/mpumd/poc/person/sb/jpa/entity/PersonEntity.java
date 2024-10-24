package com.mpumd.poc.person.sb.jpa.entity;

import com.mpumd.poc.person.sb.jpa.converter.ISO8601ZonedDateTimeConverter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.Temporal;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PERSON")
@Entity
public class PersonEntity {
    @Id
    @Column(name = "id", length = 36, unique = true)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "birth_date")
    @Convert(converter = ISO8601ZonedDateTimeConverter.class)
    private ZonedDateTime birthDate;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "nationality")
    private String nationality;
}

