package com.mpumd.poc.person.sb.jpa.entity;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.sb.jpa.converter.ISO8601ZonedDateTimeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
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

    @ElementCollection
    @CollectionTable(
            name = "GENDERS",
            joinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "change_date") // name of key map
    @Column(name = "gender")            // name of value map
    @Enumerated(EnumType.STRING)
//    @MapKeyJdbcType(TimestampJdbcType.class)
//    @MapKeyJavaType(LocalDateTimeJavaType.class)
    private Map<LocalDateTime, Gender> genderChangeHistory;

    @Column(name = "birth_date")
    @Convert(converter = ISO8601ZonedDateTimeConverter.class)
    private ZonedDateTime birthDate;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "nationality")
    // TODO we can use this enum type here and use the jakarta converter to convert in string at persist
    // @Enumerated(EnumType.STRING)
    private String nationality;
}

