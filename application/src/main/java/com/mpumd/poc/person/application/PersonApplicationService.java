package com.mpumd.poc.person.application;

import com.mpumd.poc.person.application.exception.PersonAlreadyExistException;
import com.mpumd.poc.person.application.exception.PersonNotFoundException;
import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class PersonApplicationService {

    @NonNull
    private final PersonPersistanceRepository personPersistanceRepository;

    public UUID register(PersonRegistrationCommand cmd) {
        Person person = Person.register(cmd);
        var searchQuery = new PersonSearchQuery(person);
        if (personPersistanceRepository.isExist(searchQuery)) {
            throw new PersonAlreadyExistException(searchQuery.firstName(), searchQuery.lastName());
        }

        personPersistanceRepository.push(person);
        return person.id();
    }

    public void changeSex(UUID id, Gender sex, LocalDateTime changeDate) {
        Person person = personPersistanceRepository.pull(id).orElseThrow(
                () -> new PersonNotFoundException(id));

        person.changeSex(sex, changeDate);
    }
}
