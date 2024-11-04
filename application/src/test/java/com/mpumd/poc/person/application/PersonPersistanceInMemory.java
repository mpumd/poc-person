package com.mpumd.poc.person.application;

import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import lombok.Getter;

import java.util.*;
import java.util.function.Predicate;

public class PersonPersistanceInMemory implements PersonPersistanceRepository {

    @Getter
    private List<Person> persons = new ArrayList<>();

    @Override
    public Optional<Person> pull(UUID uuid) {
        return persons.stream()
                .filter(p -> p.id().equals(uuid))
                .findFirst();
    }

    @Override
    public boolean isExist(PersonSearchQuery personQuery) {
        Predicate<Person> personPredicate = p -> p.firstName().equals(personQuery.firstName());
        personPredicate.and(p -> p.lastName().equals(personQuery.lastName()));
        personPredicate.and(p -> p.birthDate().equals(personQuery.birthDate()));
        personPredicate.and(p -> p.birthPlace().equals(personQuery.birthPlace()));

        var genderMap = Map.of(personQuery.birthDate().toLocalDateTime(), personQuery.gender());
        personPredicate.and(p -> p.genderChangeHistory().entrySet().contains(genderMap));

        return this.persons.stream().anyMatch(personPredicate);
    }

    @Override
    public void push(Person person) {
        this.persons.add(person);
    }
}