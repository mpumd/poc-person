package com.mpumd.poc.person.application.command;

import com.mpumd.poc.person.application.builder.PersonRegistrationCommandBuilder;
import com.mpumd.poc.person.application.builder.PersonRegistrationCommandBuilders;
import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import org.jilt.Builder;
import org.jilt.BuilderStyle;

import java.time.ZonedDateTime;

@Builder(style = BuilderStyle.STAGED, packageName = "com.mpumd.poc.person.application.builder")
public record PersonRegistrationCommand(
        String firstName,
        String lastName,
        ZonedDateTime birthDate,
        String birthPlace,
        Gender gender,
        Nationality nationality
) {

    // fluent API
    public static PersonRegistrationCommandBuilders.FirstName builder() {
        return PersonRegistrationCommandBuilder.personRegistrationCommand();
    }
}
