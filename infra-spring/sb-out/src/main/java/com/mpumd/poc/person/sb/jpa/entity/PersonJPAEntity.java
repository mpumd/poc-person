package com.mpumd.poc.person.sb.jpa.entity;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.sb.jpa.converter.ISO8601ZonedDateTimeConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

@Table(name = "PERSON")
@Entity
public class PersonJPAEntity {

    @Id
    @Column(name = "id", length = 36, unique = true)
    UUID id;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @ElementCollection
    @CollectionTable(
            name = "GENDERS",
            joinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "change_date") // name of key map
    @Column(name = "gender")            // name of value map
    @Enumerated(EnumType.STRING)
//    @MapKeyJdbcType(TimestampJdbcType.class)
//    @MapKeyJavaType(LocalDateTimeJavaType.class)
    Map<LocalDateTime, Gender> genderChangeHistory;

    @Column(name = "birth_date")
    @Convert(converter = ISO8601ZonedDateTimeConverter.class)
    ZonedDateTime birthDate;

    @Column(name = "birth_place")
    String birthPlace;

    @Column(name = "nationality")
    // TODO we can use this enum type here and use the jakarta converter to convert in string at persist
//    @Enumerated(EnumType.STRING)
    String nationality;
}

