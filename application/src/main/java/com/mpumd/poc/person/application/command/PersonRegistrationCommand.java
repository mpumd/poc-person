package com.mpumd.poc.person.application.command;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.application.builder.PersonRegistrationCommandBuilder;
import com.mpumd.poc.person.application.builder.PersonRegistrationCommandBuilders;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jilt.Builder;
import org.jilt.BuilderStyle;

import java.time.ZonedDateTime;

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

    @Builder(style = BuilderStyle.STAGED, packageName = "com.mpumd.poc.person.application.builder")
    public PersonRegistrationCommand(String firstName, String lastName, ZonedDateTime birthDate, String birthPlace, Gender gender, Nationality nationality) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.gender = gender;
        this.nationality = nationality;
    }

    // fluent API
    public static PersonRegistrationCommandBuilders.FirstName builder() {
        return PersonRegistrationCommandBuilder.personRegistrationCommand();
    }
}
