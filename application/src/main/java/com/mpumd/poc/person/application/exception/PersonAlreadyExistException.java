package com.mpumd.poc.person.application.exception;

import lombok.experimental.StandardException;


@StandardException
public class PersonAlreadyExistException extends IllegalStateException {

    public PersonAlreadyExistException(String firstName, String lastName) {
        super(String.format("%s %s already exist !", firstName, lastName));
    }
}
