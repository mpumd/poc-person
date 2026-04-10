package com.mpumd.poc.person.application.exception;

import lombok.experimental.StandardException;


@StandardException
public class PersonAlreadyExistException extends IllegalStateException {

    public PersonAlreadyExistException(String firstName, String lastName) {
        super("%s %s already exist !".formatted(firstName, lastName));
    }
}
