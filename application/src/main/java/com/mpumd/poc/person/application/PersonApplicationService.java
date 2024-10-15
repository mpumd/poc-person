package com.mpumd.poc.person.application;

import com.mpumd.poc.person.application.exception.PersonAlreadyExistException;
import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class PersonApplicationService {

    @NonNull
    private final PersonPersistanceRepository personPersistanceRepository;

    public UUID register(PersonRegistrationCommand cmd) {
        // FIXME build Aggregat root and AFTER build query
        var searchQuery = PersonSearchQuery.builder()
                .firstName(cmd.firstName())
                .lastName(cmd.lastName())
                .birthDate(cmd.birthDate())
                .birthPlace(cmd.birthPlace())
                .gender(cmd.gender())
                .build();

        if (personPersistanceRepository.isExist(searchQuery)) {
            throw new PersonAlreadyExistException(searchQuery.firstName(), searchQuery.lastName());
        }

        Person register = Person.register(cmd);
        personPersistanceRepository.push(register);

        return register.id();
    }
}
