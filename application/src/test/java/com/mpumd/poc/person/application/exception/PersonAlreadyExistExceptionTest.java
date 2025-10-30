package com.mpumd.poc.person.application.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonAlreadyExistExceptionTest {

    @Test
    void sneakyThrowDefaultConstructor() {
        assertThatThrownBy(() -> {
            throw new PersonAlreadyExistException();
        })
                .isInstanceOf(IllegalStateException.class)
                .hasNoCause()
                .hasMessage(null);
    }

    @Test
    void sneakyThrowWithFirstNameLastNameInMessage() {
        assertThatThrownBy(() -> {
            throw new PersonAlreadyExistException("john", "rambo");
        })
                .isInstanceOf(IllegalStateException.class)
                .hasNoCause()
                .hasMessage("john rambo already exist !");
    }

    @Test
    void sneakyThrowWithMessage() {
        assertThatThrownBy(() -> {
            throw new PersonAlreadyExistException("Person already exist!");
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Person already exist!")
                .hasNoCause();
    }

    @Test
    void sneakyThrowWithCause() {
        Throwable cause = new IllegalArgumentException("Underlying cause");

        assertThatThrownBy(() -> {
            throw new PersonAlreadyExistException(cause);
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Underlying cause")
                .cause()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Underlying cause");
    }

    @Test
    void sneakyThrowWithMessageAndCause() {
        String errorMessage = "Person already exist!";
        Throwable cause = new IllegalCallerException("Underlying cause");

        assertThatThrownBy(() -> {
            throw new PersonAlreadyExistException(errorMessage, cause);
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Person already exist!")
                .cause()
                .isInstanceOf(IllegalCallerException.class)
                .hasMessage("Underlying cause");
    }
}