package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.utils.builder.PersonRegistrationCommandBuilder;
import com.mpumd.poc.person.context.utils.builder.PersonRegistrationCommandBuilders;
import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import org.jilt.Builder;
import org.jilt.BuilderStyle;

import java.time.ZonedDateTime;

import static com.mpumd.poc.person.context.utils.ObjectsUtils.notBlank;
import static com.mpumd.poc.person.context.utils.ObjectsUtils.notNull;

@Builder(style = BuilderStyle.STAGED, packageName = "com.mpumd.poc.person.context.utils.builder")
public record PersonRegistrationCommand(
        String firstName,
        String lastName,
        ZonedDateTime birthDate,
        String birthPlace,
        Gender gender,
        Nationality nationality
) {

    public PersonRegistrationCommand {
        firstName = notBlank(firstName, "firstName must not be empty");
        lastName = notBlank(lastName, "lastName must not be empty");
        birthPlace = notBlank(birthPlace, "birthPlace must not be empty");
        birthDate = notNull(birthDate, "birthDate must not be null");
        gender = notNull(gender, "gender must not be null");
        nationality = notNull(nationality, "nationality must not be null");
    }

    // fluent API
    public static PersonRegistrationCommandBuilders.FirstName builder() {
        return PersonRegistrationCommandBuilder.personRegistrationCommand();
    }
}
