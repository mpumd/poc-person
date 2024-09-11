package com.mpumd.poc.person.context.aggregat;

import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;

@AllArgsConstructor
public class Person {
    private final String fistName;
    private final String lastName;

    private final OffsetDateTime birthDate;
    private final String birthPlace; // age calculated
    private final Nationality nationality;

    private final Gender gender;


}
