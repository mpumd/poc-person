package com.mpumd.poc.person.context.query;

import com.mpumd.poc.person.context.aggregat.Gender;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

// FIXME doesn't works with record :(. After fix it, pass to record
// or put @NotNull on all fields
@Builder
@Getter
public class PersonSearchQuery {
    private final String firstName;
    private final String lastName;
    private final Gender gender;
    private final ZonedDateTime birthDate;
    private final String birthPlace;


//    public Predicate<Person> buildPredicate() {
//        List<Predicate<Person>> predicates = new ArrayList<>();
//
//        if (firstName != null && !firstName.isEmpty()) {
//            predicates.add(person -> person.getFirstName().toLowerCase().contains(firstName.toLowerCase()));
//        }
//    }
}
