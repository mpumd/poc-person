package com.mpumd.poc.person.context.query;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Person;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

// FIXME doesn't works with record :(. After fix it, pass to record
// or put @NotNull on all fields
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PersonSearchQuery {
    private final String firstName;
    private final String lastName;
    private final Gender gender;
    private final ZonedDateTime birthDate;
    private final String birthPlace;

    public PersonSearchQuery(Person person) {
        this.firstName = person.firstName();
        this.lastName = person.lastName();
        this.gender = person.genders().lastEntry().getValue();
        this.birthDate = person.birthDate();
        this.birthPlace = person.birthPlace();
    }

}
