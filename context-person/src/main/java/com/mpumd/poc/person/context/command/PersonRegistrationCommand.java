package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.builder.PersonRegistrationCommandBuilder;
import com.mpumd.poc.person.context.builder.PersonRegistrationCommandBuilders;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jilt.Builder;
import org.jilt.BuilderStyle;

import java.time.ZonedDateTime;

import static java.util.Optional.ofNullable;

@Getter
@Accessors(fluent = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PersonRegistrationCommand {
    String firstName;
    String lastName;
    ZonedDateTime birthDate;
    String birthPlace;
    Gender gender;
    Nationality nationality;

    @Builder(style = BuilderStyle.STAGED, packageName = "com.mpumd.poc.person.context.builder")
    public PersonRegistrationCommand(String firstName, String lastName, ZonedDateTime birthDate, String birthPlace, Gender gender, Nationality nationality) {
        this.firstName = ofNullable(firstName)
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("firstName must not be empty"));
        this.lastName = ofNullable(lastName)
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("lastName must not be empty"));
        this.birthDate = ofNullable(birthDate)
                .orElseThrow(() -> new IllegalArgumentException("birthDate must not be null"));
        this.birthPlace = ofNullable(birthPlace)
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("birthPlace must not be empty"));
        this.gender = ofNullable(gender)
                .orElseThrow(() -> new IllegalArgumentException("gender must not be null"));
        this.nationality = ofNullable(nationality)
                .orElseThrow(() -> new IllegalArgumentException("nationality must not be null"));
    }

    // fluent API
    public static PersonRegistrationCommandBuilders.FirstName builder() {
        return PersonRegistrationCommandBuilder.personRegistrationCommand();
    }
}