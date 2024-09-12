package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.command.PersonRegisterCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class Person {
    private final String firstName;
    private final String lastName;
//    private final Gender gender;
//    private final OffsetDateTime birthDate;
//    private final String birthPlace; // age calculated
//    private final Nationality nationality;
//    private final PhysicalAppearance physicalAppearance;

    private Person(PersonRegisterCommand cmd) {
        this.firstName = Optional.ofNullable(cmd.firstName())
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("firstName must not be empty"));
        this.lastName = Optional.ofNullable(cmd.lastName())
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("lastName must not be empty"));
    }

    public static Person register(PersonRegisterCommand cmd) {
        return new Person(cmd);
    }

    //    private final Career career;
//
//    private final Personality personality;
//
//    private final Hobby hobby;
//
//    private final SocialLife socialLife;
//
//    private final LifeStyle lifeStyle;
//
//    private final LifePurpose lifePurpose;
//
//    private final Challenge challenge;
//
//    private final SignificantPossessions significantPossessions;

}
