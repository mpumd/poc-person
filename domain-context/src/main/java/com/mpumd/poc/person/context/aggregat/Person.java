package com.mpumd.poc.person.context.aggregat;

import java.time.OffsetDateTime;

public class Person {
    private final String fistName;
    private final String lastName;

    private final OffsetDateTime birthDate;
    private final String birthPlace;
    private final Nationality nationality;

    public Person(String fistName, String lastName, OffsetDateTime birthDate, String birthPlace, Nationality nationality) {
        this.fistName = fistName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.nationality = nationality;
    }
}
