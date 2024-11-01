package com.mpumd.poc.person.context;

import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;

import java.util.Optional;
import java.util.UUID;

public interface PersonPersistanceRepository {

    Optional<Person> pull(UUID uuid);

    boolean isExist(PersonSearchQuery person);

    void push(Person person);

}
