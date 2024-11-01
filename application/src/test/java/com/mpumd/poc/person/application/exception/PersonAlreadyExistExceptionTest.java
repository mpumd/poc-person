package com.mpumd.poc.person.application.exception;

import org.junit.jupiter.api.Test;

import static lombok.Lombok.sneakyThrow;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonAlreadyExistExceptionTest {

    @Test
    void sneakyThrowDefaultConstructor() {
        assertThatThrownBy(() -> sneakyThrow(new PersonAlreadyExistException()))
                .isInstanceOf(IllegalStateException.class)
                .hasNoCause()
                .hasMessage(null);
    }

    @Test
    void sneakyThrowWithFirstNameLastNameInMessage() {
        assertThatThrownBy(() -> sneakyThrow(new PersonAlreadyExistException("john", "rambo")))
                .isInstanceOf(IllegalStateException.class)
                .hasNoCause()
                .hasMessage("john rambo already exist !");
    }

    @Test
    void sneakyThrowWithMessage() {
        assertThatThrownBy(() -> sneakyThrow(new PersonAlreadyExistException("Person already exist!")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Person already exist!")
                .hasNoCause();
    }

    @Test
    void sneakyThrowWithCause() {
        Throwable cause = new IllegalArgumentException("Underlying cause");

        assertThatThrownBy(() -> sneakyThrow(new PersonAlreadyExistException(cause)))
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

        assertThatThrownBy(() -> sneakyThrow(new PersonAlreadyExistException(errorMessage, cause)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Person already exist!")
                .cause()
                .isInstanceOf(IllegalCallerException.class)
                .hasMessage("Underlying cause");
    }
}