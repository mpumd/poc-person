package com.mpumd.poc.person.application.exception;

import lombok.experimental.StandardException;

import java.util.UUID;

@StandardException
public class PersonNotFoundException extends IllegalArgumentException {
    public PersonNotFoundException(UUID id) {
        super("The person with if %s doesn't exist !".formatted(id));
    }

}
