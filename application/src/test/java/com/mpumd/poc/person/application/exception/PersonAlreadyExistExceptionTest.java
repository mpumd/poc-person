package com.mpumd.poc.person.application.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonAlreadyExistExceptionTest {

    @Test
    void throwExDefaultConstructor() {
        assertThatThrownBy(() -> throwEx(new PersonAlreadyExistException()))
                .isInstanceOf(IllegalStateException.class)
                .hasNoCause()
                .hasMessage(null);
    }

    @Test
    void throwExWithMessage() {
        assertThatThrownBy(() -> throwEx(new PersonAlreadyExistException("Person not found!")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Person not found!")
                .hasNoCause();
    }

    @Test
    void throwExWithCause() {
        Throwable cause = new IllegalArgumentException("Underlying cause");

        assertThatThrownBy(() -> throwEx(new PersonAlreadyExistException(cause)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Underlying cause")
                .cause()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Underlying cause");
    }

    @Test
    void throwExWithMessageAndCause() {
        String errorMessage = "Person not found!";
        Throwable cause = new IllegalCallerException("Underlying cause");

        assertThatThrownBy(() -> throwEx(new PersonAlreadyExistException(errorMessage, cause)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Person not found!")
                .cause()
                .isInstanceOf(IllegalCallerException.class)
                .hasMessage("Underlying cause");
    }

    private void throwEx(Throwable ex) throws Throwable {
        throw ex;
    }
}