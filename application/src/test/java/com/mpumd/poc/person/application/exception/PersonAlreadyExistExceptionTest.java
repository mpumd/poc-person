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
    void throwExWithFirstNameLastNameInMessage() {
        assertThatThrownBy(() -> throwEx(new PersonAlreadyExistException("john", "rambo")))
                .isInstanceOf(IllegalStateException.class)
                .hasNoCause()
                .hasMessage("john rambo already exist !");
    }

    @Test
    void throwExWithMessage() {
        assertThatThrownBy(() -> throwEx(new PersonAlreadyExistException("Person already exist!")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Person already exist!")
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
        String errorMessage = "Person already exist!";
        Throwable cause = new IllegalCallerException("Underlying cause");

        assertThatThrownBy(() -> throwEx(new PersonAlreadyExistException(errorMessage, cause)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Person already exist!")
                .cause()
                .isInstanceOf(IllegalCallerException.class)
                .hasMessage("Underlying cause");
    }

    private void throwEx(Throwable ex) throws Throwable {
        throw ex;
    }
}